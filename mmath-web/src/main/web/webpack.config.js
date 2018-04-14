var webpack = require('webpack');
var path = require('path');
var CopyWebpackPlugin = require('copy-webpack-plugin');


var BUILD_DIR = path.resolve(__dirname, '../resources/web/public');
var APP_DIR = path.resolve(__dirname, 'src');


var API_ROOT = "";
var API_URL = API_ROOT + '/api';

var constants = {
    MMATH: {
        BETTER_THAN: JSON.stringify(API_URL + "/better-than/{0}/{1}"),
        SEARCH: JSON.stringify(API_URL + "/fighters/query"),
        FIGHTS: JSON.stringify(API_URL + "/fights/{0}"),
        FIGHTER_BY_HASH: JSON.stringify(API_URL + "/fighter/{0}")
    },
    EVENTS: {
        BY_ID: JSON.stringify(API_URL + "/events/{0}"),
        INCOMING: JSON.stringify(API_URL + "/events/incoming"),
        FIGHTS: JSON.stringify(API_URL + "/events/{0}/fights"),
        EVENT_FILTERS: JSON.stringify(API_URL+"/events/organization-filters"),
    },
    STATS: {
        GET_ALL_CATEGORIES: JSON.stringify(API_URL + "/stats"),
        GET_CATEGORY: JSON.stringify(API_URL + "/stats/{0}"),
        GET_ENTRIES: JSON.stringify(API_URL + "/stats/entries/{0}"),
        GET_FOR_FIGHTER: JSON.stringify(API_URL + "/stats/for-fighter/{0}")
    }
};

var config = {
    entry: [APP_DIR + '/jsx/index.jsx', APP_DIR + '/less/main.less'],
    output: {
        path: BUILD_DIR,
        filename: 'bundle.js'
    },
    plugins: [
        new webpack.DefinePlugin({
            'API': constants
        }),

        new CopyWebpackPlugin([
            {from: APP_DIR + '/index.html'}
        ], {
            copyUnmodified: true
        })
    ],
    module: {
        rules: [
            {
                test: /\.(woff|woff2|eot|ttf|svg)$/,
                loader: 'url-loader?limit=100000'
            },
            {
                test: /\.(jpg|png|svg|gif)$/,
                loader: 'file-loader?name=images/[name].[ext]',
                include: APP_DIR + '/images',
            },
            {
                test: /\.jsx?/,
                include: APP_DIR,
                loader: 'babel-loader'
            },
            {test: /\.css$/, loader: 'style-loader!css-loader',},
            {
                test: /main.less$/,
                use: [{
                    loader: "style-loader" // creates style nodes from JS strings
                }, {
                    loader: "css-loader" // translates CSS into CommonJS
                }, {
                    loader: "less-loader"
                }]
            }
        ]
    }
};

module.exports = config;