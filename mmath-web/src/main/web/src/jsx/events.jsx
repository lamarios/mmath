import React from 'react';
import EventService from './services/EventService.jsx';
import EventChip from './event-chip.jsx';

export default class Events extends React.Component {
    constructor(props) {
        super(props);

        console.log(this.props);

        this.state = {events: []};

        this.eventService = new EventService();
    }

    componentDidMount() {
        this.eventService.getUpcoming()
            .then(res => {
                console.log(res.data);
                this.setState({events: res.data});
            });
    }

    render() {
        return (
            <div className="Events">
                <h1>Recent &amp; Upcoming events</h1>
                {this.state.events.map((event, i) => {
                        const link = '/events/'+event.id+'/fights';
                        return <EventChip  onClick={()=> this.props.history.push(link)} key={event.id} event={event} />
                    }
                )}
            </div>
        );
    }
}