var React = require('react');

var FighterTypeAhead = require('./fighter-type-ahead');
var FighterChip = require('./fighter-chip');

var FighterSearch = React.createClass({
                                          //loads fighters based on the text search
                                          loadFighters: function (name) {

                                              $.ajax({
                                                         method: 'POST',
                                                         url: '/api/fighters/query',
                                                         dataType: 'json',
                                                         data: JSON.stringify({'name': name}),
                                                         cache: false,
                                                         success: function (data) {
                                                             console.log(data);
                                                             this.setState({fighters: data});
                                                         }.bind(this)
                                                     });
                                          },
                                          //initial state
                                          getInitialState: function () {
                                              return {fighters: [], query: '', selected: null};
                                          },
                                          //Triggers the search
                                          handleSearch: function (e) {
                                              console.log(e);
                                              var query = e.target.value;
                                              if (query.length >= 3) {
                                                  clearTimeout(this.state.timeout);
                                                  this.state.timeout =
                                                      setTimeout(function () {
                                                          this.loadFighters(query)
                                                      }.bind(this), 300);
                                              } else {
                                                  this.setState({fighters: []});
                                              }
                                          },

                                          //Handles the click when the user clicks on the fighter
                                          // he wants to choose
                                          handleFighterSelection: function (fighter) {
                                              this.setState({selected: fighter});
                                              this.props.fighterSelected(fighter);
                                          },
                                          //Remove the selected fighter
                                          removeSelected: function () {
                                              this.setState({selected: null, fighters: []});
                                              this.props.fighterSelected(null);

                                          },
                                          render: function () {
                                              if (this.state.selected === null) {
                                                  return (
                                                      <div className="fighter-search">
                                                          <input className="form-control" type="text"
                                                                 onKeyUp={this.handleSearch}
                                                                 placeholder="Search for fighter"
                                                          />
                                                          {this.state.fighters.length > 0 ?
                                                           <FighterTypeAhead
                                                               onClick={this.handleFighterSelection}
                                                               fighters={this.state.fighters}/>
                                                              : '' }
                                                      </div>
                                                  );
                                              } else {
                                                  return (
                                                      <FighterChip fighter={this.state.selected}
                                                                   removable={true}
                                                                   onClick={this.removeSelected}/>
                                                  );
                                              }
                                          }
                                      });

module.exports = FighterSearch;