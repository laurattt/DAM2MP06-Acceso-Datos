const fs = require('fs');
const path = require('path');
const { MongoClient } = require('mongodb');
const xml2js = require('xml2js');
require('dotenv').config();

// ruta xml (astronomy bla bla)
const xmlFilePathAstronomy = path.join(__dirname, '../../data/Posts.xml');

// lectura de xml 
async function parseXMLFile(filePath) {
  try {
    console.log(`hola te leo tu fichero xml`)
    const xmlData = fs.readFileSync(filePath, 'utf-8');
    const parser = new xml2js.Parser({ 
      explicitArray: false,
      mergeAttrs: true
    });
    
    return new Promise((resolve, reject) => {
      parser.parseString(xmlData, (err, result) => {
        if (err) {
          reject(err);
        } else {
          resolve(result);
        }
      });
    });
  } catch (error) {
    console.error('Error llegint o analitzant el fitxer XML:', error);
    throw error;
  }
}


// procesar datos y transformar para mongo
function processAstronomyData(data) {
  const astronomyQ = Array;

}

// datos a mongodb
async function loadDataToMongoDB() {
  
  const uri = process.env.MONGODB_URI || 'mongodb://root:password@localhost:27017/';
  const client = new MongoClient(uri);
  
  try {
    await client.connect();
    console.log('Connectat a MongoDB');
    
    const database = client.db('astronomy_db');
    const collection = database.collection('astronomy');
    
    // Llegir i analitzar el fitxer XML
    console.log('Llegint el fitxer XML...');
    const xmlData = await parseXMLFile(xmlFilePathAstronomy);
    
    // Processar les dades
    console.log('Processant les dades...');
    const astronomyQuestion = processAstronomyData(xmlData);
    
    // Eliminar dades existents (opcional)
    console.log('Eliminant dades existents...');
    await collection.deleteMany({});
    
    // Inserir les noves dades
    console.log('Inserint dades a MongoDB...');
    const result = await collection.insertMany(astronomyQuestion);
    
    console.log(`${result.insertedCount} documents inserits correctament.`);
    console.log('Dades carregades amb èxit!');
    
  } catch (error) {
    console.error('Error carregant les dades a MongoDB:', error);
  } finally {
    await client.close();
    console.log('Connexió a MongoDB tancada');
  }
  
}


// Executar la funció principal
//loadDataToMongoDB();

parseXMLFile(xmlFilePathAstronomy);
