import React from 'react';
import FighterDetails from './fighter-details.jsx';

export default class FighterChip extends React.Component {

    constructor(props) {
        super(props);


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


        var className = "fighter-chip small";

        // if (this.props.small === true) {
        //     className += " small";
        // }

        return (
            <div className="fighter-chip-wrapper">
                {this.props.unknown === undefined ?
                    <div className={className} onClick={this.toggleDetails}>
                        <div className="info">
                            <p className="name">
                                {this.props.fighter.name}
                            </p>
                            <p className="record">
                                {this.props.fighter.record}
                            </p>
                            {this.props.removable &&
                            <div className="remove" onClick={this.props.onClick}>
                                <i className="fa fa-times"
                                   aria-hidden="true"></i>
                            </div>

                            }
                        </div>
                    </div>
                    :
                    <div className={className}>
                        <div className="info">
                            <p className="name">
                                Unknown Fighter
                            </p>
                        </div>
                    </div>
                }
                {
                    this.state.details === true &&
                    <FighterDetails fighter={this.props.fighter} onCloseClick={this.toggleDetails}/>
                }
            </div>
        );
    }

}

