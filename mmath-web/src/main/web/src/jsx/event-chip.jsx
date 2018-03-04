import React from 'react';
import browserHistory from 'react-router-dom';

export default class EventChip extends React.Component {

    constructor(props) {
        super(props);
    }


    render() {
        const event = this.props.event;
        const date = event.date.split('T')[0];
        const link = '/event/' + event.id + '/fights';
        return (
            <div className="EventChip" onClick={this.props.onClick}>
                <div className="date">{date}</div>
                <div className="name">{event.name}</div>
            </div>
        )
    }
}

