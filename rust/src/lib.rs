use std::cmp::{max, min};
use wasm_bindgen::prelude::*;

mod utils;

// When the `wee_alloc` feature is enabled, use `wee_alloc` as the global
// allocator.
#[cfg(feature = "wee_alloc")]
#[global_allocator]
static ALLOC: wee_alloc::WeeAlloc = wee_alloc::WeeAlloc::INIT;

#[wasm_bindgen]
extern {
    fn alert(s: &str);
}

#[wasm_bindgen]
pub fn greet() {
    alert("Hello, tank_game!");
}

use rand::prelude::*;
use crate::utils::{empty_vec_2d, linear_gradient};

#[wasm_bindgen]
pub fn value_noise(width: usize, height: usize, octaves: u16) -> Box<Vec<Vec<f32>>> {

    let mut x_frequency: usize = 20;
    let mut y_frequency: usize = 20;

    let mut max_height = 20 as f32;

    let mut data = empty_vec_2d(width, height, 0 as f32);
    for _ in 0..octaves {
        let mut temp_data = empty_vec_2d(width, height, 0 as f32);

        let delta_x = max(width / x_frequency, 1);
        let delta_y = max(height / y_frequency, 1);

        // Generate random points
        for k in 0..x_frequency {
            for m in 0..y_frequency {
                temp_data[k * delta_x][m * delta_y] = rand::random::<f32>() * max_height;
            }
        }
        // Interpolation
        for k in 0..width {
            let x_distance = (k % delta_x);
            let previous_x = k - x_distance;
            let next_x = min(k + delta_x - x_distance, width - 1);
            for m in 0..height {
                let y_distance = (m % delta_y);
                let previous_y = m - y_distance;
                let next_y = min(m + delta_y - y_distance, height - 1);
                let first = linear_gradient(
                    temp_data[previous_x][previous_y],
                    temp_data[next_x][previous_y],
                    x_distance as f32 / delta_x
                );
                let second = linear_gradient(
                    temp_data[previous_x][next_y],
                    temp_data[next_x][next_y],
                    x_distance as f32 / delta_x
                );
                temp_data[k][m] = linear_gradient(
                    first, second,
                    y_distance as f32 / delta_y
                );
            }
        }

        data = data.iter().enumerate().map(|(i, x)| {
            x.iter().enumerate().map(|(j, y)| {
                y + temp_data[i][j]
            })
        }).collect();
        max_height /= 2;

        y_frequency = min(y_frequency << 1, width);
        x_frequency = min(x_frequency << 1, height);
    }
    data
}
