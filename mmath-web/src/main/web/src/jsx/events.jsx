import React from 'react';
import EventService from './services/EventService.jsx';
import EventChip from './event-chip.jsx';
import EventFilter from './event-filter.jsx';


export default class Events extends React.Component {
    constructor(props) {
        super(props);

        this.state = {events: [], page: 1, selected: null};

        this.eventService = new EventService();
        this.getEvents = this.getEvents.bind(this);

        this.onFilterChange = this.onFilterChange.bind(this);
        this.loadMore = this.loadMore.bind(this);
    }

    componentDidMount() {
    }

    getEvents() {
        this.eventService.getUpcoming(this.state.page, this.state.selected)
            .then(res => {
                var events = this.state.events;
                events.push.apply(events, res.data)
                this.setState({events: events});
            });
    }

    loadMore() {
        this.setState({page: this.state.page + 1}, () => {
            this.getEvents();
        });
    }


    onFilterChange(selected) {
        var organizations = [];
        if (selected['all']) {
            organizations = null;

        } else {
            Object.keys(selected).forEach((org, index) => {
                if (org !== 'all' && selected[org]) {
                    organizations.push(org);
                }
            })

        }

        this.setState({page: 1, selected: organizations, events: []}, () => {
            this.getEvents()
        });
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
                {this.state.events.length === this.state.page * 20 &&
                <a className="loadMore" onClick={this.loadMore}>more</a>}
            </div>
        );
    }
}