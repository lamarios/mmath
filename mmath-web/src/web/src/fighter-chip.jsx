var React = require('react');

var FighterChip =
    React.createClass({
                          render: function () {
                              var picture = {
                                  backgroundImage: 'url('+this.props.fighter.picture+')'
                              };

                              return (
                                  <div className="fighter-chip" >
                                      <div className="picture" style={picture}></div>
                                      <span className="name">
                                          {this.props.fighter.name}
                                      </span>
                                      <span className="record">
                                          {this.props.fighter.wins} - {this.props.fighter.losses} - {this.props.fighter.draws} - {this.props.fighter.nc}
                                      </span>
                                      { this.props.removable ?
                                        <div className="remove" onClick={this.props.onClick}>
                                            <i className="fa fa-times"
                                               aria-hidden="true"></i>
                                        </div>
                                          :
                                        ''
                                      }
                                  </div>
                              );
                          }

                      });

module.exports = FighterChip;