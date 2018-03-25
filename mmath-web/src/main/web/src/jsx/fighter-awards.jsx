import React from 'react';
import StatsService from './services/StatsService.jsx';
import Laurel from './laurel.jsx';
import {NavLink} from 'react-router-dom';

export default class FighterAwards extends React.Component {
    constructor(props) {
        super(props);

        this.statsService = new StatsService();

        this.state = {awards: []};

    }

    componentDidMount() {
        this.statsService.getFighterStats(this.props.fighter.id)
            .then(res => this.setState({awards: res.data}));
    }

    render() {
        return (
            <div className="FighterAwards">
                {this.state.awards.length > 0 && <div>
                    <h2>Awards</h2>
                    {this.state.awards.map((award, index) => {

                        const laurelClass = award.rank == 0 ? 'champ award' : 'award';
                        const rank = award.rank == 0? 'C' : award.rank;
                        const link = '/stats/'+award.category.id;
                        return (
                            <div key={award.category.id} className={laurelClass}>
                                <NavLink to={link}>
                                    <span className='rank'>{ rank }</span>
                                    <span className='category'>{ award.category.name }</span>

                                    <Laurel/>
                                </NavLink>
                            </div>

                        );
                    })}
                </div>
                }
            </div>

        );
    }
}