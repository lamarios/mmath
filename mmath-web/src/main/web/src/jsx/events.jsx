import React from 'react';
import EventService from './services/EventService.jsx';
import EventChip from './event-chip.jsx';
import EventFilter from './event-filter.jsx';


export default class Events extends React.Component {
    constructor(props) {
        super(props);

        console.log(this.props);

        this.state = {events: []};

        this.eventService = new EventService();


        this.onFilterChange = this.onFilterChange.bind(this);
    }

    componentDidMount() {
        this.eventService.getUpcoming()
            .then(res => {
                console.log(res.data);
                this.setState({events: res.data});
            });
    }


    onFilterChange(selected) {
        console.log('selected', selected);
    }

    render() {
        return (
            <div className="Events">
                <h1>Recent &amp; Upcoming events</h1>
                <EventFilter onChange={this.onFilterChange}/>
                {this.state.events.map((event, i) => {
                        const link = '/events/' + event.id + '/fights';
                        return <EventChip onClick={() => this.props.history.push(link)} key={event.id} event={event}/>
                    }
                )}
            </div>
        );
    }
}