const WebSocket = require('ws');
const InputManager = require('./src/inputManager.js');

require('dotenv').config();

// Crear conexión
const ws = new WebSocket(process.env.SERVER_URI); 

let lastDirection = null;
let inactivityTimeout = null;

// Conexión abierta
ws.on('open', () => {
    console.log('Conexión con el servidor exitosa');

    ws.send(JSON.stringify({ hello: 'world' }));

    const inputManager = new InputManager((movementState) => {

        const currentDirection = inputManager.parseToDirection() || 'NONE';

        // limpiar timeout anterior
        if (inactivityTimeout) {
            clearTimeout(inactivityTimeout);
        }

        if (currentDirection !== lastDirection) {
            lastDirection = currentDirection;

            console.log(currentDirection);

            ws.send(JSON.stringify({
                direction: currentDirection,
                timestamp: Date.now()
            }));
        }

        inactivityTimeout = setTimeout(() => {
            if (lastDirection !== 'NONE') {
                lastDirection = 'NONE';

                console.log('NONE');

                ws.send(JSON.stringify({
                    direction: 'NONE',
                    timestamp: Date.now()
                }));
            }
        }, 100);
    });
});

// Recibir mensajes
ws.on('message', (data) => {
    try {
        const message = JSON.parse(data);
        console.log('Mensaje del servidor: ', data.toString());

        if (message.event === 'FINISHED') {
            ws.close();
            process.exit();
        }
    } catch (err) {
        console.log('Mensaje del servidor (raw):', data.toString());
    }
});

// Detectar cierre de conexión
ws.on('close', () => {
    console.log('Conexión cerrada');
});

// Detectar errores
ws.on('error', (err) => {
    console.error('Error en la conexión:', err);
});