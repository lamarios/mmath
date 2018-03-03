import React from 'react';
import {render} from 'react-dom';
import {BrowserRouter, Route} from 'react-router-dom';
import Mmath from './mmath.jsx';



let images = require.context("../images/", true, /^\.\/.*\.(png|gif|svg)/);


String.prototype.format = function () {
    let s = this,
        i = arguments.length;

    while (i--) {
        s = s.replace(new RegExp('\\{' + i + '\\}', 'gm'), arguments[i]);
    }
    return s;
};

render((
    <BrowserRouter>
        <div>
            <Route exact path="/" component={Mmath} />
            <Route path="/:fighter1/vs/:fighter2" component={Mmath} />
        </div>
    </BrowserRouter>
), document.getElementById('content'));