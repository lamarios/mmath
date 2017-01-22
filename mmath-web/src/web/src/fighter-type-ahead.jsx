var React = require('react');

var FighterTypeAhead =
    React.createClass({
                          render: function () {
                              return (
                                  <ul className="fighters">
                                      {

                                          this.props.fighters.map(
                                              function (fighter) {
                                                  var style = {
                                                      backgroundImage: 'url('
                                                                       + fighter.picture
                                                                       + ')'
                                                  };

                                                  return <li
                                                      onClick={this.props.onClick.bind(
                                                          null, fighter)}
                                                      key={fighter.id}
                                                      data-id={fighter.id}>
                                                      <div className="icon" style={style}></div>
                                                      {fighter.name}</li>
                                              }.bind(this))

                                      }
                                  </ul>
                              );
                          }
                      });
module.exports = FighterTypeAhead