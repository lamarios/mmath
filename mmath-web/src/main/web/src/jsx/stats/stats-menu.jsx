import React from 'react';
import StatsService from '../services/StatsService.jsx';
import {NavLink} from 'react-router-dom';


export default class StatsMenu extends React.Component {

    constructor() {
        super();

        this.state = {categories: []}

        this.statsService = new StatsService();
    }


    componentDidMount() {
        this.statsService.getCategories()
            .then(res => {
                this.setState({categories: res.data});
            });
    }

    render() {
        const menuClass = this.props.showMenu === true ? 'show':'';
        return (
            <div className='StatsMenu'>
                <ul className={menuClass}>
                    <li>Select stats type</li>
                    {this.state.categories.map((stat, i) => {
                        const link = "/stats/" + stat.id;
                        return (
                            <li key={stat.id} onClick={this.props.closeMenu}>
                                <NavLink to={link}>{stat.name}</NavLink>
                            </li>
                        )
                    })}

                </ul>
            </div>
        )
    }

}



