pub fn set_panic_hook() {
    // When the `console_error_panic_hook` feature is enabled, we can call the
    // `set_panic_hook` function at least once during initialization, and then
    // we will get better error messages if our code ever panics.
    //
    // For more details see
    // https://github.com/rustwasm/console_error_panic_hook#readme
    #[cfg(feature = "console_error_panic_hook")]
    console_error_panic_hook::set_once();
}

pub fn empty_vec_2d<T: Copy>(width: usize, height: usize, default: T) -> Vec<Vec<T>> {
    let mut temp_data = Vec::with_capacity(width);
    for _ in 0..width {
        let current: Vec<T> = std::iter::repeat(default).take(height).collect();
        temp_data.push(current);
    }
    temp_data
}

pub fn linear_gradient(start: f32, end: f32, progress: f32) -> f32 {
    start + (end - start) * progress
}
