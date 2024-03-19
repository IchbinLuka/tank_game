config.devServer.headers = {
    "Access-Control-Allow-Origin": "*",
    "Content-Security-Policy": "*",
    ...config.devServer.headers
}
config.devServer.server = "https"