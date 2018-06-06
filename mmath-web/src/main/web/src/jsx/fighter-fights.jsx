import React from 'react';

import {NavLink} from 'react-router-dom';

export default class FighterFights extends React.Component {


    render() {
        if (typeof this.props.fights !== 'undefined' && this.props.fights.length > 0) {
            return (<div className="FighterFights">
                <h2>{this.props.title}</h2>
                <table className="fights">
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
                        this.props.fights.slice(0).reverse().map(
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
                                const eventLink = '/events/' + fight.eventId + "/fights";
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
            </div>);
        } else {
            return null;
        }
    }
}
