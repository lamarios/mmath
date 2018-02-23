var React = require('react');
var createReactClass = require('create-react-class');

var FighterDetails =
    createReactClass({
        render: function () {
            var fighter = this.props.fighter;


            var picture = {
                backgroundImage: 'url(' + fighter.picture + ')'
            };

            return (
                <div className="fighter-details">

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
                        <div className="table-responsive">
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
                                    fighter.fights.slice(0).reverse().map(
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


                                            var key = fight.date + fight.opponent;

                                            return (
                                                <tr key={key} className={rowClass}>
                                                    <td>{fight.opponent}</td>
                                                    <td>{fight.event}</td>
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
    });

module.exports = FighterDetails;