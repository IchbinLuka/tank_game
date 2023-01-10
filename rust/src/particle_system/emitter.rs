pub mod emitter {
    use wasm_bindgen::prelude::wasm_bindgen;

    use crate::particle_system::Vect3;

    #[wasm_bindgen]
    pub enum Emitter {
        SphereEmitter,
        
    }
}