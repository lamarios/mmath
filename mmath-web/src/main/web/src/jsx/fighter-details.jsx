import React from 'react';
import MmathService from './services/MmathService.jsx';
import FighterAwards from './fighter-awards.jsx';
import FighterFights from "./fighter-fights.jsx";

export default class FighterDetails extends React.Component {

    constructor() {
        super();

        this.mmathService = new MmathService();
        this.state = {fights: []};
        this.groupBy = this.groupBy.bind(this);
    }


    componentDidMount() {

        this.mmathService.getFights(this.props.fighter.id)
            .then(res => {
                this.setState({
                    fights: res.data
                });
            });
    };

    groupBy(xs, key) {
        return xs.reduce(function (rv, x) {
            (rv[x[key]] = rv[x[key]] || []).push(x);
            return rv;
        }, {});
    };

    render() {
        var fighter = this.props.fighter;

        var sortedFights = this.groupBy(this.state.fights, 'type');
        console.log('sorted fights', sortedFights);

        return (
            <div className="fighter-details">
                <div className="overlay" onClick={this.props.onCloseClick}>
                </div>
                <div className={"details"}>
                    <div className="data">
                        <div className="remove" onClick={this.props.onCloseClick}>
                            <i className="fa fa-times" aria-hidden="true"></i>
                        </div>

                        <div className="title">
                            <h1>{fighter.name}</h1>
                            {fighter.nickname != undefined && fighter.nickname.length > 0 &&
                            <p>"{fighter.nickname}"</p>
                            }
                        </div>

                        <div className="info">
                            <p>
                                <label>Record:</label> {fighter.wins} - {fighter.losses} - {fighter.draws} - {fighter.nc}
                            </p>
                            <p><label>Birthday: </label> {fighter.birthday}</p>
                            <p><label>Weight: </label> {fighter.weight}</p>
                            <p><label>Height: </label> {fighter.height}</p>
                        </div>
                        <FighterAwards fighter={fighter}/>
                        <div className="fights">
                            <FighterFights fights={sortedFights.UPCOMING} title="Upcoming Fights"/>
                            <FighterFights fights={sortedFights.PRO} title="Pro Fights"/>
                            <FighterFights fights={sortedFights.PRO_EXHIBITION} title="Pro Exhibition Fights"/>
                            <FighterFights fights={sortedFights.AMATEUR} title="Amateur Fights"/>
                            <FighterFights fights={sortedFights.EXHIBITION} title="Exhibition Fights"/>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

