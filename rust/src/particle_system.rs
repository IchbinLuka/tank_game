use crate::wasm_bindgen;

use self::emitter::emitter::Emitter;

mod emitter;

#[wasm_bindgen]
pub struct ParticleHelper {
    gravity: Vect3, 
    particle_lifetime: f32, 
    emitter: Emitter
}

#[wasm_bindgen]
pub struct Vect3 {
    x: f32, 
    y: f32, 
    z: f32
}

impl Into<(f32, f32, f32)> for Vect3 {
    fn into(self) -> (f32, f32, f32) {
        (self.x, self.y, self.z)
    }
}

#[wasm_bindgen]
impl ParticleHelper {

    pub fn new(gravity: Vect3, particle_lifetime: f32, emitter: Emitter) -> Self {
        ParticleHelper { gravity, particle_lifetime, emitter }
    }

    pub fn step(&self, positions: &mut [f32], velocities: &mut [f32], colors: &mut [f32], sizes: &mut [f32], delta_time: f32) {
        assert_eq!(positions.len() % 3, 0);
        let position_iter = positions.chunks_mut(3);
        let velocities_iter = velocities.chunks_mut(3);
        
        for (p, v) in position_iter.zip(velocities_iter) {
            if let [x_pos, y_pos, z_pos] = p  {
                if let [x_v, y_v, z_v] = v {
                    *x_pos += *x_v * delta_time;
                    *y_pos += *y_v * delta_time;
                    *z_pos += *z_v * delta_time;

                    *x_v += self.gravity.x * delta_time;
                    *y_v += self.gravity.y * delta_time;
                    *z_v += self.gravity.z * delta_time;
                }
            }
        }
    }

}
