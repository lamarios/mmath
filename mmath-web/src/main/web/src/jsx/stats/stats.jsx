import React from 'react';
import StatsMenu from './stats-menu.jsx';
import {Switch, Route} from 'react-router-dom';
import StatsDetails from './stats-details.jsx';

export default class Stats extends React.Component {

    constructor() {
        super();
        this.state = {showMenu: false};
        this.showMenu = this.showMenu.bind(this);
        this.closeMenu = this.closeMenu.bind(this);
    }


    showMenu() {
        this.setState({showMenu: true});
    }

    closeMenu() {
        this.setState({showMenu: false});
    }


    render() {
        return (<div className="Stats">
            <div className="row">
                <div className="col-md-3">
                    <StatsMenu showMenu={this.state.showMenu} closeMenu={this.closeMenu}/>
                </div>
                <div className="col-md-9">
                    <Switch>
                        <Route path="/stats/:cat"
                               render={(props) => (<StatsDetails {...props} titleClicked={this.showMenu}/>)}/>
                    </Switch>
                </div>
            </div>
        </div>);
    }
}