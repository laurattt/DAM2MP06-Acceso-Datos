const fs = require('fs');
const path = require('path');
const { MongoClient } = require('mongodb');
const xml2js = require('xml2js');
const entities = require('entities');
const winston = require('winston');
require('dotenv').config();

// xml de astronomy
const xmlFilePath = path.join(__dirname, '../../data/Posts.xml');

// logs
const logDir = path.join(__dirname, '../../data/logs');

if (!fs.existsSync(logDir)) {
  fs.mkdirSync(logDir, { recursive: true });
}

const logger = winston.createLogger({
  level: 'info',
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.printf(({ timestamp, level, message }) => {
      return `${timestamp} [${level.toUpperCase()}]: ${message}`;
    })
  ),
  transports: [
    new winston.transports.Console(),
    new winston.transports.File({
      filename: path.join(logDir, 'exercici1.log')
    })
  ]
});

// xml lectura
async function parseXMLFile(filePath) {
  try {
    const xmlData = fs.readFileSync(filePath, 'utf-8');
    const parser = new xml2js.Parser({ 
      explicitArray: false,
      mergeAttrs: true
    });

    return new Promise((resolve, reject) => {
      parser.parseString(xmlData, (err, result) => {
        if (err) reject(err);
        else resolve(result);
      });
    });

  } catch (error) {
    logger.error('Error llegint XML: ' + error.message);
    throw error;
  }
}

// data process
function processAstronomyData(data) {
  const rows = Array.isArray(data.posts.row) 
    ? data.posts.row 
    : [data.posts.row];

  return rows
    .filter(row => row.PostTypeId === '1')
    .map(row => {

      const bodyDecodificado = entities.decodeHTML(row.Body || '');

      return {
        questionId: row.Id,
        creationDate: new Date(row.CreationDate),
        score: parseInt(row.Score) || 0,
        viewCount: parseInt(row.ViewCount) || 0,
        body: bodyDecodificado,
        title: row.Title,
        tags: row.Tags,
        answerCount: parseInt(row.AnswerCount) || 0,
        commentCount: parseInt(row.CommentCount) || 0
      };
    })
    .sort((a, b) => b.viewCount - a.viewCount)
    .slice(0, 10000);
}

// mongodb cargar 
async function loadDataToMongoDB() {

  const uri = process.env.MONGODB_URI || 'mongodb://root:password@localhost:27017/';
  const client = new MongoClient(uri);

  try {
    await client.connect();
    logger.info('Connectat a MongoDB');

    const database = client.db('astronomy_db');
    const collection = database.collection('astronomy');

    logger.info('Llegint el fitxer XML...');
    const xmlData = await parseXMLFile(xmlFilePath);

    logger.info('Processant les dades...');
    const questions = processAstronomyData(xmlData);

    logger.info('Eliminant dades existents...');
    await collection.deleteMany({});

    logger.info('Inserint dades a MongoDB...');
    const result = await collection.insertMany(questions);

    logger.info(result.insertedCount + ' documents inserits correctament');
    logger.info('Dades carregades amb èxit!');

  } catch (error) {
    logger.error('Error carregant dades: ' + error.message);
  } finally {
    await client.close();
    logger.info('Connexió a MongoDB tancada');
  }
}

loadDataToMongoDB();