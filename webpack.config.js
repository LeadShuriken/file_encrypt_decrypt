
const webpack = require('webpack');
const moment = require('moment');
const yaml = require('js-yaml');
const fs = require('fs');

const CleanWebpackPlugin = require('clean-webpack-plugin').CleanWebpackPlugin;
const CopyPlugin = require("copy-webpack-plugin");
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CssMinimizerPlugin = require("css-minimizer-webpack-plugin");
const HtmlReplaceWebpackPlugin = require('html-replace-webpack-plugin');
const RemoveEmptyScriptsPlugin = require('webpack-remove-empty-scripts');


const RFOLDER = './src/main/resources/';
const APPLICATION = yaml.load(
    fs.readFileSync(`${RFOLDER}application.yml`, 'utf8')
);

module.exports = {
    mode: 'production',
    plugins: [
        new webpack.DefinePlugin({
            PRODUCTION: JSON.stringify(true),
            R53: JSON.stringify(APPLICATION.api.r53)
        }),
        new CopyPlugin({
            patterns: [{ from: `${RFOLDER}static/js/worker.js`, to: "worker.js" }]
        }),
        new RemoveEmptyScriptsPlugin(),
        new MiniCssExtractPlugin(),
        new CleanWebpackPlugin(),
        new HtmlWebpackPlugin({
            template: `${RFOLDER}static/index.html`,
            title: 'Example Application',
            inject: 'body'
        }),
        new HtmlReplaceWebpackPlugin([
            {
                pattern: 'JQUERY_VERSION',
                replacement: APPLICATION.webjars.jquery
            },
            {
                pattern: 'BOOTSTRAP_VERSION',
                replacement: APPLICATION.webjars.bootstrap
            },
            {
                pattern: 'REDIS_TTL_MAX',
                replacement: moment.duration(APPLICATION.redis.ttlMax).asHours()
            },
            {
                pattern: 'REDIS_TTL_MIN',
                replacement: moment.duration(APPLICATION.redis.ttlMin).asHours()
            }
        ])
    ],
    module: {
        rules: [{
            test: /\.css$/,
            use: [
                MiniCssExtractPlugin.loader,
                'css-loader',
            ]
        }]
    },
    optimization: {
        removeEmptyChunks: true,
        minimize: true,
        minimizer: [
            new UglifyJsPlugin(),
            new CssMinimizerPlugin()
        ]
    },
    entry: {
        aes: `${RFOLDER}static/js/aes-gcm.js`,
        handlers: {
            dependOn: ['aes'],
            import: `${RFOLDER}static/js/handlers.js`,
        },
        main: `${RFOLDER}static/css/main.css`
    }
};