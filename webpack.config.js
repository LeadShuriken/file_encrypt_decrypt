
const webpack = require('webpack');
const moment = require('moment');
const path = require('path');
const yaml = require('js-yaml');
const fs = require('fs');

const CleanWebpackPlugin = require('clean-webpack-plugin').CleanWebpackPlugin;
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CssMinimizerPlugin = require("css-minimizer-webpack-plugin");
const HtmlReplaceWebpackPlugin = require('html-replace-webpack-plugin');
const RemoveEmptyScriptsPlugin = require('webpack-remove-empty-scripts');

const CONF_FOLDER = './src/main/resources/';
const RFOLDER = './static';
const APPLICATION = yaml.load(
    fs.readFileSync(`${CONF_FOLDER}application.yml`, 'utf8')
);

module.exports = {
    mode: 'production',
    output: {
        path: path.resolve(__dirname, 'public'),
    },
    devServer: {
        port: 9000
    },
    plugins: [
        new webpack.DefinePlugin({
            PRODUCTION: JSON.stringify(true),
            R53: JSON.stringify(APPLICATION.api.r53)
        }),
        new RemoveEmptyScriptsPlugin(),
        new MiniCssExtractPlugin(),
        new CleanWebpackPlugin(),
        new HtmlWebpackPlugin({
            template: `${RFOLDER}/index.html`,
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
        aes: `${RFOLDER}/js/aes-gcm.js`,
        worker: {
            dependOn: ['aes'],
            import: `${RFOLDER}/js/worker.js`,
        },
        handlers: {
            dependOn: ['worker'],
            import: `${RFOLDER}/js/handlers.js`,
        },
        main: `${RFOLDER}/css/main.css`
    }
};