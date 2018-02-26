var React = require('react');
var createReactClass = require('create-react-class');
var FighterSearch = require('./fighter-search.jsx');

var BetterThan = createReactClass({
    getInitialState: function () {
        return {fighter1: null, fighter2: null};
    },
    fighter1Selected: function (fighter) {
        this.setState({fighter1: fighter});
    },
    fighter2Selected: function (fighter) {
        this.setState({fighter2: fighter});
    },
    render: function () {
        return (
            <div className="better-than">
                <div className="row">
                    <div className="col-md-5 fighter1">
                        <FighterSearch
                            fighterSelected={this.fighter1Selected}/>
                    </div>
                    <div className="col-md-2 vs">
                        VS.
                    </div>
                    <div className="col-md-5 fighter2">
                        <FighterSearch
                            fighterSelected={this.fighter2Selected}/>
                    </div>
                </div>
                <div className="row">
                    <div className="col-sm-12 fight-button">
                        {this.state.fighter1 !== null
                        && this.state.fighter2 !== null ?
                            <button className="btn btn-danger"
                                    onClick={this.props.triggerSearch.bind(
                                        null, this.state.fighter1,
                                        this.state.fighter2)}>
                                Fight !
                            </button>
                            :
                            ''
                        }
                    </div>
                </div>
            </div>
        );
    }
});

module.exports = BetterThan;