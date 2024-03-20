config.devServer.headers = {
    "Access-Control-Allow-Origin": "*",
    "Cache-Control": "no-cache",
    ...config.devServer.headers
}
// config.devServer.server = "https"
config.devServer.allowedHosts = "all"