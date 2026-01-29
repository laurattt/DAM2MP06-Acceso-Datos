// esto son imports de librerias (deben de estar en package.json -> dependencies )
const csv = require('csv-parser');
const fs = require('fs');
const path = require('path');
require('dotenv').config();

// variables constantes etc etc
const DATA_SUBFOLDER = 'steamreviews';
const CSV_GAMES_FILE_NAME = 'games.csv';
const CSV_REVIEWS_FILE_NAME = 'reviews.csv';

// lectura csv (en segundo plano)
async function readCSV(filePath) {
    const results = [];
    return new Promise((resolve, reject) => { // promesa que devuelve = resuelto(results) o reject(error)
        fs.createReadStream(filePath)
            .pipe(csv())
            .on('data', (data) => results.push(data))
            .on('end', () => resolve(results))
            .on('error', reject);
    });
}

// Funció per fer la petició a Ollama amb més detalls d'error

async function analyzeSentiment(text) {
    try {
        console.log('\n -------------------- Enviant petició a Ollama... ------------------------');
        console.log('Model:', process.env.CHAT_API_OLLAMA_MODEL_TEXT), `\n`;
        
        const response = await fetch(`${process.env.CHAT_API_OLLAMA_URL}/generate`, { // llamado a la api, await espera que se cumpla la promesa(Asincrona) que es generada por fetch()
            method: 'POST', // peticion http (metodo http (POST) + URI(process.env.CHAT_API_OLLAMA_URL))
            headers: {
                'Content-Type': 'application/json', // Se le indica que se usará json 
            },
            body: JSON.stringify({ // peticion al ollama en json 
                model: process.env.CHAT_API_OLLAMA_MODEL_TEXT,
                prompt: `Analyze the sentiment of this text and respond with only one word (positive/negative/neutral): "${text}"`, //prompt por defecto o text
                stream: false
            })
        });

        /* se hace una pausa a causa del AWAIT, esta pausa terminará cuando la promesa creada por el fetch finalice, (fetch devuelve la promesa)
        entonces esta promesa se basa en la peticion que se le está haciendo al OLLAMA, debe devolverla RESOLVE o REJECT*/

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status} ${response.statusText}`); 
        }

        const data = await response.json(); // espera a que la promesa se cumpla y devuevle rta 
        
        // Depuració de la resposta -> esto es lo que dice ollama tipo JSON, 
        
//----- console.log('Resposta completa d\'Ollama:', JSON.stringify(data, null, 2)); // EL JSON QUE REGRESA ES UNO POR DEFECTO >:( -> data.response (ya definida por la api)
        
        // Verificar si tenim una resposta vàlida
        if (!data || !data.response) {
            throw new Error('La resposta d\'Ollama no té el format esperat');
        }

        return data.response.trim().toLowerCase();
    } catch (error) {
        console.error('Error detallat en la petició a Ollama:', error);
        console.error('Detalls adicionals:', {
            url: `${process.env.CHAT_API_OLLAMA_URL}/generate`,
            model: process.env.CHAT_API_OLLAMA_MODEL_TEXT,
            promptLength: text.length
        });
        return 'error';
    }
}

async function main() {
    console.log(`aqui estoy eh`)
    try {
        // Obtenim la ruta del directori de dades
        const dataPath = process.env.DATA_PATH;

        // Validem les variables d'entorn necessàries - validacion de modelos cargados
        if (!dataPath) {
            throw new Error('La variable d\'entorn DATA_PATH no està definida');
        }
        if (!process.env.CHAT_API_OLLAMA_URL) {
            throw new Error('La variable d\'entorn CHAT_API_OLLAMA_URL no està definida');
        }
        if (!process.env.CHAT_API_OLLAMA_MODEL_TEXT) {
            throw new Error('La variable d\'entorn CHAT_API_OLLAMA_MODEL_TEXT no està definida');
        }

        // Construïm les rutes completes als fitxers CSV
        const gamesFilePath = path.join(__dirname, dataPath, DATA_SUBFOLDER, CSV_GAMES_FILE_NAME); // fichero juegos
        const reviewsFilePath = path.join(__dirname, dataPath, DATA_SUBFOLDER, CSV_REVIEWS_FILE_NAME); // fichero review 

        // Validem si els fitxers existeixen - validacion existencia ficheros 
        if (!fs.existsSync(gamesFilePath) || !fs.existsSync(reviewsFilePath)) {
            throw new Error('Algun dels fitxers CSV no existeix');
        }

        // Llegim els CSVs
        const games = await readCSV(gamesFilePath);
        const reviews = await readCSV(reviewsFilePath);


        /*LISTA DE JUEGOOOOOOOOOOOOS AQUI 

        console.log('\n=== Llista de Jocs ===');
        for (const game of games) {
            console.log(`Codi: ${game.appid}, Nom: ${game.name}`);
        }*/
        

        // Iterem per les primeres 10 reviews i analitzem el sentiment
        console.log('\n=== Anàlisi de Sentiment de Reviews ===');
        const reviewsToAnalyze = reviews.slice(0, 2);
        

        /*
        CODE ORG 

        for (const review of reviewsToAnalyze) {
            console.log(`\nProcessant review: ${review.id}`);
            const sentiment = await analyzeSentiment(review.content); //RESPUESTA OLLAMAAAAA
            console.log(`Review ID: ${review.id}`);
            console.log(`Joc ID: ${review.app_id}`);
            console.log(`Contingut: ${review.content.substring(0, 100)}...`);
            console.log(`Sentiment (Ollama): ${sentiment}`);
            console.log('------------------------');
        }
        console.log(`\nNOMÉS AVALUEM LES DUES PRIMERES REVIEWS`);*/

        // EXERCICI2 ---------------------------------------------------------------------------------------
        const gamesToAnalyze = games.slice(0,2);
        

        // extraccion datos a JSON 
        // primero for games y luego review, para que compare entre 2 reviews por juego 
        
        // cambio de logica del for para agregar contador (condicionales entre reviews)
        for (const game of gamesToAnalyze){ //por cada 1 juego
            for(const review of reviewsToAnalyze){ // ve 2 reviews
                const sentimentNewReview = await analyzeSentiment(game.content) // opinion/mensaje ollama

                console.log(`Hola, generando respuesta a peticion :D \n`)

                const jsonNewExercici2 = {
                    timestamp: new Date().toISOString(), //timestamp de cuando se analizó la review 
                    game:[
                        {
                        "appid:": game.appid,
                        "name:": game.name,
                        "statistics:":{
                            "positive:": review.is_positive
                        }
                }],
                    sentiment: sentimentNewReview,
                    model: process.env.CHAT_API_OLLAMA_MODEL_TEXT
                }

                console.log(JSON.stringify(jsonNewExercici2, null, 2));
            }
        }

            

        // TEST(?)
        /* 
        for (const review of reviewsToAnalyze){
            const sentimentNewReview = await analyzeSentiment(review.content) // opinion/mensaje ollama

            console.log(`Hola, generando respuesta a peticion :D \n`)

            for(const game of gamesToAnalyze){ // iteracion de los juegos (2) por qué solo se estan iterando dos juegos si en ningun lado le asigno el valor de DOS 
                //console.log(game)

                // condicionales de "is_positive"

                const jsonNewExercici2 = {
                    timestamp: new Date().toISOString(), //timestamp de cuando se analizó la review 
                    game:[
                        {
                        "appid:": game.appid,
                        "name:": game.name,
                        "statistics:":{
                            "positive:": review.is_positive
                        }
                }],
                    sentiment: sentimentNewReview,
                    model: process.env.CHAT_API_OLLAMA_MODEL_TEXT
                }

                console.log(JSON.stringify(jsonNewExercici2, null, 2));
            }

        }*/
        

        /*
        for (const review of reviewsToAnalyze) {
            const sentiment = await analyzeSentiment(review.content);

            const reviewJson = {
                review_id: review.id,
                app_id: review.app_id,
                content_preview: review.content.substring(0, 100),
                sentiment: sentiment,
                model: process.env.CHAT_API_OLLAMA_MODEL_TEXT,
                source: 'ollama',
                analyzed_at: new Date().toISOString()
            };

            console.log(JSON.stringify(reviewJson, null, 2));
        }
        */

    



     } catch (error) {
        console.error('Error durant l\'execució:', error.message);
    }

}



// Executem la funció principal
main();
