var BetterThan = require('./better-than');
var FightResult = require('./fight-result');
var Loader = require('./loader');

var Mmath = React.createClass({
                                  getInitialState: function () {
                                      return {fighter1VsFighter2: null, loading: false};
                                  },
                                  triggerSearch: function (fighter1, fighter2) {
                                      console.log(fighter1);
                                      console.log(fighter2);

                                      this.setState({loading: true, fighter1VsFighter2: null});

                                      $.ajax({
                                                 method: 'GET',
                                                 url: '/api/better-than/' + fighter1.id + '/'
                                                      + fighter2.id,
                                                 dataType: 'json',
                                                 cache: false,
                                                 success: function (data) {
                                                     console.log(data);
                                                     this.setState({
                                                                       loading: false,
                                                                       fighter1VsFighter2: data
                                                                   });
                                                 }.bind(this)
                                             });
                                  },
                                  render: function () {
                                      return (
                                          <div>
                                              <BetterThan triggerSearch={this.triggerSearch}/>
                                              <div className="results row">
                                                  <div className="col-sm-12 column">
                                                      {this.state.loading ? <Loader/>: ''}
                                                      
                                                      {
                                                          this.state.fighter1VsFighter2 !== null
                                                              ?
                                                          this.state.fighter1VsFighter2.length > 0 ?
                                                          <FightResult
                                                              results={this.state.fighter1VsFighter2}/>
                                                              : <p className="nope">Nope...</p>
                                                              :
                                                          ''
                                                      }
                                                  </div>
                                              </div>
                                          </div>
                                      )
                                  }

                              });

ReactDOM.render(
    <Mmath/>
    ,
    document.getElementById('content')
);