import React from 'react';
import {render} from 'react-dom';
import {BrowserRouter, Route} from 'react-router-dom';
import Mmath from './mmath.jsx';
import MainMenu from './main-menu.jsx';
import Events from './events.jsx';
import Event from './event.jsx';
import Stats from './stats/stats.jsx';

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
            <Route exact path="/stats" component={Stats} />
            <Route exact path="/stats/:cat" component={Stats} />
        </div>
    </BrowserRouter>
), document.getElementById('content'));