var React = require('react');
var createReactClass = require('create-react-class');
var FighterDetails = require('./fighter-details.jsx');
var FighterChip =
    createReactClass({
        getInitialState: function () {
            return {details: false};
        },
        /**
         * Toggle fighter details
         */
        toggleDetails: function () {
            this.setState({details: !this.state.details});
        },

        /**
         * Renders the content
         * @returns {XML}
         */
        render: function () {
            var picture = {
                backgroundImage: 'url(' + this.props.fighter.picture + ')'
            };

            var className = "fighter-chip";

            if (this.props.small === true) {
                className += " small";
            }

            return (
                <div>
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

    });

module.exports = FighterChip;