var React = require('react');
var createReactClass = require('create-react-class');

var FighterChip = require('./fighter-chip.jsx');

var FightResult = createReactClass({

    render: function () {
        return (
            <div className="result">
                {
                    this.props.results.map(
                        function (fighter, i, arr) {
                            console.log(fighter);
                            return (
                                <div key={fighter.id} className="result-step">
                                    <FighterChip fighter={fighter} small={true} removable={false}/>
                                    { i < arr.length - 1 ?
                                        <div
                                            className="separator">

                                        </div>
                                        : ''}
                                </div>
                            )
                        })
                }
            </div>
        );
    }
});

module.exports = FightResult;