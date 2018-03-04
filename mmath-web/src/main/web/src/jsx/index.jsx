import React from 'react';
import {render} from 'react-dom';
import {BrowserRouter, Route} from 'react-router-dom';
import Mmath from './mmath.jsx';
import MainMenu from './main-menu.jsx';
import Events from './events.jsx';
import Event from './event.jsx';
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
            <MainMenu />
            <Route exact path="/" component={Mmath} />
            <Route exact path="/:fighter1/vs/:fighter2" component={Mmath} />
            <Route exact path="/events" component={Events} />
            <Route exact path="/events/:id/fights" component={Event} />
        </div>
    </BrowserRouter>
), document.getElementById('content'));