import React from 'react';
import EventService from './services/EventService.jsx';
import FighterChip from './fighter-chip.jsx';

export default class Event extends React.Component {

    constructor(props) {
        super(props);
        this.state = {event: null, fights: []};


        this.eventService = new EventService();
    }

    componentDidMount() {
        this.eventService.getById(this.props.match.params.id)
            .then(event => {
                this.eventService.getFights(event.data.id)
                    .then(fights => {
                        this.setState({event: event.data, fights: fights.data});
                    })
            });
    }

    render() {
        return (<div className="Event">
            {this.state.event ? (
                <div>
                    <h1>{this.state.event.name}</h1>
                    <div className="fights">
                        {this.state.fights.map((fight, i) => {
                           const fightLink = '/'+fight.fighter1.id+'/vs/'+fight.fighter2.id;
                            return(
                            <div key={fightLink} className="row event-fight">
                                <div className="col-md-5">
                                    <FighterChip fighter={fight.fighter1}/>
                                </div>
                                <div className="col-md-2 vs">
                                    <button onClick={()=>{ this.props.history.push(fightLink)}} className="btn btn-danger btn-lg">vs.</button>
                                </div>
                                <div className="col-md-5">
                                    <FighterChip fighter={fight.fighter2}/>
                                </div>
                            </div>
                        )})}
                    </div>
                </div>
            ) : ""}
        </div>);
    }

}