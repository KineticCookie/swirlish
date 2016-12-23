var path = require('path');
var webpack = require('webpack');
var debug = process.env.NODE_ENV !== "production";

module.exports = {
    devtool: debug ? 'eval' : null,
    entry: debug ? [
        'webpack-dev-server/client?http://localhost:3000',
        'webpack/hot/only-dev-server',
        './src/client'
    ] : [
        './src/client.js'
    ],
    output: {
        path: path.join(__dirname, 'dist'),
        filename: 'bundle.js',
        publicPath: '/static/'
    },
    plugins: debug ? [
        new webpack.HotModuleReplacementPlugin()
    ] : [
        new webpack.optimize.DedupePlugin(),
        new webpack.optimize.OccurenceOrderPlugin(),
        new webpack.optimize.UglifyJsPlugin({ mangle: false, sourcemap: false })
    ],
    module: {
        loaders: [
            {
                test: /\.js$/,
                loaders: ['react-hot', 'babel'],
                include: path.join(__dirname, 'src')
            },
            { test: /\.css$/, loader: "style-loader!css-loader" },
            { test: /\.less$/, loader: "style!css!less" },
            { test: /\.woff(2)?(\?v=[0-9]\.[0-9]\.[0-9])?$/, loader: "url-loader?limit=10000&minetype=application/font-woff" },
            { test: /\.(ttf|eot|svg)(\?v=[0-9]\.[0-9]\.[0-9])?$/, loader: "file-loader" },
            { test: /\.jpe?g$|\.gif$|\.png$/i, loader: "file-loader" },
            { loader: 'json-loader', test: /\.json$/}
        ]//,
        //noParse: ['ws']
    },
    //externals: ['ws'],
    node: {
        fs: 'empty',
        tls: 'empty'
    }
};
