import { GLTFLoader } from 'three/addons/loaders/GLTFLoader.js';

function addTurbine(x, y) {
    const loader = new GLTFLoader();
    return new Promise((resolve, reject) => {
        loader.load('models/turbine.gltf', function (turbine) {
            turbine.scene.position.set(x, -100, y);
            turbine.scene.scale.set(5, 5, 5);
            resolve(turbine);
        }, undefined, reject);
    });
}

export { addTurbine };