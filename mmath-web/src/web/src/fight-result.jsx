var React = require('react');

var FighterChip = require('./fighter-chip');

var FightResult = React.createClass({

                                        render: function () {
                                            return (
                                                <div className="result">
                                                    {
                                                        this.props.results.map(
                                                            function (fighter, i, arr) {
                                                                console.log(fighter);
                                                                return (
                                                                    <div className="result-step">
                                                                        <FighterChip
                                                                            fighter={fighter}
                                                                            removable={false}/>
                                                                        { i < arr.length - 1 ?
                                                                          <div
                                                                              className="separator">
                                                                              <i className="fa fa-chevron-down"
                                                                                 aria-hidden="true"></i>
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