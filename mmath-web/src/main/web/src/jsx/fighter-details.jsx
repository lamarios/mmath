import React from 'react';
import MmathService from './services/MmathService.jsx';
import {NavLink} from 'react-router-dom';

export default class FighterDetails extends React.Component {

    constructor() {
        super();

        this.mmathService = new MmathService();
        this.state = {fights: []};
    }


    componentDidMount() {

        this.mmathService.getFights(this.props.fighter.id)
            .then(res => {
                this.setState({
                    fights: res.data
                });
            });
    }

    render() {
        var fighter = this.props.fighter;


        var picture = {
            backgroundImage: 'url(' + fighter.picture + ')'
        };

        return (
            <div className="fighter-details">
                <div className="background-picture" style={picture}>
                    <div className="overlay" />
                </div>
                <div className="remove" onClick={this.props.onCloseClick}>
                    <i className="fa fa-times" aria-hidden="true"></i>
                </div>

                <div className="title">
                    <h1>{fighter.name}</h1>
                    {fighter.nickname != undefined && fighter.nickname.length > 0 &&
                    <p>"{fighter.nickname}"</p>
                    }
                </div>

                <div className="picture" style={picture}>
                </div>

                <div className="info">
                    <p><label>Record:</label> {fighter.wins} - {fighter.losses} - {fighter.draws} - {fighter.nc}</p>
                    <p><label>Birthday: </label> {fighter.birthday}</p>
                    <p><label>Weight: </label> {fighter.weight}</p>
                    <p><label>Height: </label> {fighter.height}</p>
                </div>

                <div className="fights">
                    <h2>Fights</h2>
                    <div>
                        <table>
                            <thead>
                            <tr>
                                <th>Opponent</th>
                                <th>Event</th>
                                <th>Result</th>
                                <th>Round</th>
                                <th>Time</th>
                            </tr>
                            </thead>
                            <tbody>
                            {
                                //using slice to duplicate the array
                                this.state.fights.slice(0).reverse().map(
                                    function (fight, i, arr) {
                                        //row class
                                        var rowClass = '';
                                        if (fight.result === 'FIGHTER_1_WIN') {
                                            rowClass = 'win';
                                        } else if (fight.result === 'FIGHTER_2_WIN') {
                                            rowClass = 'loss';
                                        } else {
                                            rowClass = 'draw';
                                        }


                                        const key = fight.date + fight.opponent;
                                        const eventLink = '/events/'+fight.eventId+"/fights";
                                        return (
                                            <tr key={key} className={rowClass}>
                                                <td>{fight.opponent}</td>
                                                <td><NavLink to={eventLink}>{fight.event}</NavLink></td>
                                                <td>{fight.winMethod}</td>
                                                <td>{fight.winRound}</td>
                                                <td>{fight.winTime}</td>
                                            </tr>
                                        )
                                    })
                            }
                            </tbody>
                        </table>
                    </div>
                </div>

            </div>
        )
    }
}

