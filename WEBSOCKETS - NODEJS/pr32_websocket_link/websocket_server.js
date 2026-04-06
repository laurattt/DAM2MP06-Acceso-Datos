const WebSocket = require('ws');
const { MongoClient } = require('mongodb');
const winston = require('winston');
const crypto = require('crypto');
require('dotenv').config();

// logger
const logger = winston.createLogger({
    level: 'debug',
    format: winston.format.combine(
        winston.format.timestamp(),
        winston.format.printf(({ timestamp, level, message }) => `${timestamp} ${level}: ${message}`)
    ),
    transports: [
        new winston.transports.Console(),
        new winston.transports.File({ filename: './data/logs/server.log' })
    ],
});

// config
const SERVER_PORT = process.env.SERVER_PORT;
const INACTIVITY_LIMIT = 5000;
const VELOCIDAD = 1.5;

const client = new MongoClient(process.env.MONGODB_URI); 

async function iniciarServidor() {
    try {
        await client.connect();
        logger.info('Conexión con MongoDB establecida');

        const database = client.db('movement2d_db');
        const collection = database.collection('movement2d');

        // websocket
        const wss = new WebSocket.Server({ port: SERVER_PORT });
        logger.info(`Servidor arrancado en puerto ${SERVER_PORT}`);

        wss.on('connection', (ws) => {

            let gameTimer = null;
            let gameActive = true;

            logger.info('Nuevo cliente conectado');
            ws.send(JSON.stringify({ msg: 'Conexión aceptada' }));

            const gameId = crypto.randomUUID();
            logger.info(`ID de la partida: ${gameId}`);

            ws.on('message', async (data) => {
                if (!gameActive) return;

                let message;

                // validamos json (ultimo bug aqui)
                try {
                    message = JSON.parse(data);
                } catch (err) {
                    logger.error('JSON inválido');
                    return;
                }

                logger.info(`Mensaje recibido: ${data}`);

                // verif direcciones
                const validDirections = ['UP', 'DOWN', 'LEFT', 'RIGHT', 'NONE'];
                if (!validDirections.includes(message.direction)) {
                    logger.warn('Dirección inválida');
                    return;
                }

                // control inactividad (timer)
                if (gameTimer) {
                    clearTimeout(gameTimer);
                    gameTimer = null;
                    logger.info("Timer cancelado: el jugador sigue activo");
                }

                if (message.direction === 'NONE') {
                    logger.info(`Iniciando timer de inactividad (${INACTIVITY_LIMIT / 1000}s)...`);

                    gameTimer = setTimeout(async () => {
                        gameActive = false;
                        logger.warn(`PARTIDA ACABADA: Inactividad detectada en la partida ${gameId}`);

                        const distanciaTotal = await finalizarPartida(collection, gameId);

                        ws.send(JSON.stringify({ event: 'FINISHED', distanciaTotal }));
                        ws.send(JSON.stringify({ event: 'GAME_OVER', reason: 'timeout' }));

                    }, INACTIVITY_LIMIT);
                }

                // data a mongo
                const movementJson = {
                    gameId: gameId,
                    direction: message.direction,
                    timestampClient: message.timestamp,
                    timestampProcessed: Date.now()
                };

                console.log(JSON.stringify(movementJson));

                const result = await collection.insertOne(movementJson);
                logger.info(`Documento insertado: ${result.insertedId}`);
            });

            ws.on('close', () => {
                logger.info('Conexión cerrada con cliente');

                if (gameTimer) {
                    clearTimeout(gameTimer);
                }
            });

            ws.on('error', (err) => {
                logger.error(`Error en la conexión con cliente: ${err}`);
            });
        });

    } catch (err) {
        logger.error(`Error al iniciar servidor: ${err.message}`);
    }
}

iniciarServidor();


// data resumen fin de partida
async function finalizarPartida(collection, gameId) {
    logger.info(`Calculando resumen final para la partida: ${gameId}...`);

    let x = 0;
    let y = 0;

    try {
        const movimientos = await collection
            .find({ gameId: gameId })
            .sort({ timestampClient: 1 })
            .toArray();

        if (movimientos.length < 2) {
            logger.info("No hay suficientes movimientos.");
            return 0;
        }

        for (let i = 0; i < movimientos.length - 1; i++) {
            const actual = movimientos[i];
            const siguiente = movimientos[i + 1];

            const deltaTiempo = (siguiente.timestampClient - actual.timestampClient) / 1000;
            const distancia = VELOCIDAD * deltaTiempo;

            switch (actual.direction) {
                case 'UP':
                    y += distancia;
                    break;
                case 'DOWN':
                    y -= distancia;
                    break;
                case 'LEFT':
                    x -= distancia;
                    break;
                case 'RIGHT':
                    x += distancia;
                    break;
            }
        }

        logger.info(`Resultado final - X=${x.toFixed(2)}, Y=${y.toFixed(2)}`);

        const distanciaTotal = Math.sqrt((x * x) + (y * y));

        logger.info(`Distancia total: ${distanciaTotal.toFixed(2)}`);

        return distanciaTotal;

    } catch (err) {
        logger.error(`Error en finalizarPartida: ${err.message}`);
        return 0;
    }
}

