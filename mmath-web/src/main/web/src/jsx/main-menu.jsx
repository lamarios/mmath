import React from 'react';
import { NavLink } from 'react-router-dom';

export default class MainMenu extends React.Component {

    render() {
        return (<ul className="MainMenu">
            <li><NavLink exact to="/" >Mmath</NavLink></li>
            <li><NavLink to="/events" >Events</NavLink></li>
            <li><NavLink to="/stats/GLASS_CANNON" >Stats</NavLink></li>
        </ul>);
    }

}