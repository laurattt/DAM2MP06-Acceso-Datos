const fs = require('fs');
const path = require('path');
const { MongoClient } = require('mongodb');
const PDFDocument = require('pdfkit');
const winston = require('winston');
require('dotenv').config();

// logs
const logDir = path.join(__dirname, '../../data/logs');
if (!fs.existsSync(logDir)) fs.mkdirSync(logDir, { recursive: true });

const logger = winston.createLogger({
  level: 'info',
  format: winston.format.combine(
    winston.format.timestamp(),
    winston.format.printf(({ timestamp, level, message }) => `${timestamp} [${level.toUpperCase()}]: ${message}`)
  ),
  transports: [
    new winston.transports.Console(),
    new winston.transports.File({ filename: path.join(logDir, 'exercici2.log') })
  ]
});

// pdf
const outDir = path.join(__dirname, '../../data/out');
if (!fs.existsSync(outDir)) fs.mkdirSync(outDir, { recursive: true });

// MongoDB URI
const uri = process.env.MONGODB_URI || 'mongodb://root:password@localhost:27017/';
const client = new MongoClient(uri);

async function generatPdf() {
  try {
    await client.connect();
    logger.info('Connectat a MongoDB');

    const db = client.db('astronomy_db');
    const collection = db.collection('astronomy');

    // consulta viewcount
    const agg = await collection.aggregate([
      { $group: { _id: null, avgViewCount: { $avg: "$viewCount" } } } // query mongodb
    ]).toArray();
    const avgViewCount = agg[0].avgViewCount;

    const preguntasMayorMedia = await collection.find({ viewCount: { $gt: avgViewCount } }).toArray();
    console.log('Preguntes amb ViewCount > mitjana:', preguntasMayorMedia.length);

    // informe1.pdf
    const pdf1 = new PDFDocument();
    pdf1.pipe(fs.createWriteStream(path.join(outDir, 'informe1.pdf')));
    pdf1.fontSize(16).text('Preguntes amb ViewCount > mitjana', { underline: true });
    preguntasMayorMedia.forEach(q => pdf1.fontSize(12).text(q.title));
    pdf1.end();

    // consulta letras/palabras en el titulo
    const keywords = ["pug", "wig", "yak", "nap", "jig", "mug", "zap", "gag", "oaf", "elf"];
    const regex = new RegExp(keywords.join("|"), "i");

    const preguntasKeywords = await collection.find({ title: { $regex: regex } }).toArray(); //query mongodb
    console.log('Preguntes amb keywords al títol:', preguntasKeywords.length);

    // informe2.pdf
    const pdf2 = new PDFDocument();
    pdf2.pipe(fs.createWriteStream(path.join(outDir, 'informe2.pdf')));
    pdf2.fontSize(16).text('Preguntes amb keywords al títol', { underline: true });
    preguntasKeywords.forEach(q => pdf2.fontSize(12).text(q.title));
    pdf2.end();

    logger.info('PDFs generats correctament!');

  } catch (error) {
    logger.error('Error executant exercici2: ' + error.message);
  } finally {
    await client.close();
    logger.info('Connexió a MongoDB tancada');
  }
}

generatPdf();