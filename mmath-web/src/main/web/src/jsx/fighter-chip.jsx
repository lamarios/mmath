import React from 'react';
import FighterDetails from './fighter-details.jsx';

export default class FighterChip extends React.Component {

    constructor() {
        super();

        this.state = {details: false};

        this.toggleDetails = this.toggleDetails.bind(this);
    }

    /**
     * Toggle fighter details
     */
    toggleDetails() {
        this.setState({details: !this.state.details});
    }


    /**
     * Renders the content
     * @returns {XML}
     */
    render() {
        var picture = {
            backgroundImage: 'url(' + this.props.fighter.picture + ')'
        };

        var className = "fighter-chip small";

        // if (this.props.small === true) {
        //     className += " small";
        // }

        return (
            <div className="fighter-chip-wrapper">
                <div className={className} onClick={this.toggleDetails}>
                    <div className="picture" style={picture}></div>
                    <div className="info">
                        <p className="name">
                            {this.props.fighter.name}
                        </p>
                        <p className="record">
                            {this.props.fighter.wins} - {this.props.fighter.losses}
                            - {this.props.fighter.draws} - {this.props.fighter.nc}
                        </p>
                        {this.props.removable &&
                        <div className="remove" onClick={this.props.onClick}>
                            <i className="fa fa-times"
                               aria-hidden="true"></i>
                        </div>

                        }
                    </div>
                </div>
                {
                    this.state.details === true &&
                    <FighterDetails fighter={this.props.fighter} onCloseClick={this.toggleDetails}/>
                }
            </div>
        );
    }

}

