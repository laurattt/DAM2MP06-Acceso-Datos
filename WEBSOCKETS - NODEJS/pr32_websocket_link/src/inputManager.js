class InputManager {
    constructor(onChangeCallback) {
        this.movementState = { up: false, down: false, left: false, right: false };
        this.onChange = onChangeCallback;

        this.sequences = {
            '\u001b[A': 'up',
            '\u001b[B': 'down',
            '\u001b[D': 'left',
            '\u001b[C': 'right'
        };

        this.initRawMode();
        this.startIdleLoop();
    }

    initRawMode() {
        const { stdin } = process;
        if (stdin.isTTY) stdin.setRawMode(true);
        stdin.resume();
        stdin.setEncoding('utf8');

        stdin.on('data', (key) => {
            if (key === '\u0003') process.exit(); // Ctrl+C para salir
            const dir = this.sequences[key];
            if (dir) this.handleInput(dir);
        });
    }

    handleInput(dir) {
        // reset direcciones
        for (let k in this.movementState) 
            this.movementState[k] = false;

        // tecla/direccion presionada
        this.movementState[dir] = true;

        // callback
        if (this.onChange) this.onChange({ ...this.movementState }); //los tres puntos sirven para crear una "copia del objeto" para tener mejor flujo con las callbacks
    }

    // direccion actual a servidor
    parseToDirection() {
        if (this.movementState.up) 
            return "UP";
        if (this.movementState.down) 
            return "DOWN";
        if (this.movementState.left) 
            return "LEFT";
        if (this.movementState.right) 
            return "RIGHT";
        return "NONE";
    }

    // Loop que envía NONE si no hay tecla presionada (cada 100ms)
    startIdleLoop() {
        setInterval(() => {
            const anyPressed = Object.values(this.movementState).some(v => v);
            if (!anyPressed && this.onChange) {
                this.onChange({ ...this.movementState });
            }
        }, 100);
    }
}

module.exports = InputManager;