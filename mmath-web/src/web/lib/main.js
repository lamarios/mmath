/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};

/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {

/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId])
/******/ 			return installedModules[moduleId].exports;

/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			exports: {},
/******/ 			id: moduleId,
/******/ 			loaded: false
/******/ 		};

/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);

/******/ 		// Flag the module as loaded
/******/ 		module.loaded = true;

/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}


/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;

/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;

/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";

/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(0);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var BetterThan = __webpack_require__(1);
	var FightResult = __webpack_require__(7);
	var FighterSearch = __webpack_require__(3);
	var Loader = __webpack_require__(6);
	var classNames = __webpack_require__(8);

	var Mmath = React.createClass({
	  displayName: 'Mmath',

	  getInitialState: function getInitialState() {
	    return { fighter1VsFighter2: null, loading: false };
	  },
	  fighter1Selected: function fighter1Selected(fighter, isSelected) {
	    this.setState({ fighter1: fighter, isFighter1Selected: isSelected });
	  },
	  fighter2Selected: function fighter2Selected(fighter, isSelected) {
	    this.setState({ fighter2: fighter, isFighter2Selected: isSelected });
	  },
	  triggerSearch: function triggerSearch(fighter1, fighter2) {
	    console.log('fighter1', fighter1);
	    console.log('fighter2', fighter2);

	    this.setState({ loading: true, fighter1VsFighter2: null });

	    $.ajax({
	      method: 'GET',
	      url: '/api/better-than/' + fighter1.id + '/' + fighter2.id,
	      dataType: 'json',
	      cache: false,
	      success: (function (data) {
	        console.log('better than data', data);
	        console.log('omg');
	        this.setState({
	          loading: false,
	          fighter1VsFighter2: data
	        });
	      }).bind(this)
	    });
	  },
	  render: function render() {
	    var fighters = [{ backgroundImage: "url(images/fighterPlaceHolder.gif)" }, { backgroundImage: "url(images/fighterPlaceHolder.gif)" }];

	    var fighter1ClassNames = classNames({
	      'fighter-1 card align-self-end': true,
	      'selected': this.state.isFighter1Selected
	    });

	    var testClassName = "fighter-1 card align-self-end";
	    if (this.state.isFighter1Selected) {
	      testClassName += ' selected';
	    }

	    console.log(fighter1ClassNames);
	    console.log('isFighter1Selected', this.state.isFighter1Selected);

	    return React.createElement(
	      'div',
	      null,
	      React.createElement(BetterThan, { triggerSearch: this.triggerSearch }),
	      React.createElement(
	        'div',
	        { className: 'results row' },
	        React.createElement(
	          'div',
	          { className: 'col-sm-12 column' },
	          this.state.loading ? React.createElement(Loader, null) : '',
	          this.state.fighter1VsFighter2 !== null ? this.state.fighter1VsFighter2.length > 0 ? React.createElement(FightResult, {
	            results: this.state.fighter1VsFighter2 }) : React.createElement(
	            'p',
	            { className: 'nope' },
	            'Nope...'
	          ) : ''
	        )
	      ),
	      React.createElement(
	        'div',
	        { id: 'fighters-card', className: 'd-flex' },
	        React.createElement(
	          'div',
	          { className: 'fighter-wrapper w-50 d-flex flex-column mr-4' },
	          React.createElement(
	            'div',
	            { className: testClassName },
	            React.createElement(
	              'div',
	              { className: 'selection text-center p-3' },
	              React.createElement(
	                'h3',
	                { className: 'my-4' },
	                'Fighter #1'
	              ),
	              React.createElement(FighterSearch, {
	                fighterSelected: this.fighter1Selected })
	            ),
	            React.createElement(
	              'div',
	              { className: 'detail' },
	              React.createElement('div', { className: 'img-wrapper', style: fighters[0] }),
	              React.createElement(
	                'div',
	                { className: 'card-block text-right' },
	                React.createElement(
	                  'h4',
	                  { className: 'card-title name' },
	                  React.createElement(
	                    'div',
	                    { className: 'first-name' },
	                    'FirstName'
	                  ),
	                  React.createElement(
	                    'div',
	                    { className: 'last-name' },
	                    'LastName'
	                  )
	                ),
	                React.createElement(
	                  'div',
	                  { className: 'nickname' },
	                  '"Nickname nickname"'
	                ),
	                React.createElement(
	                  'h5',
	                  { className: 'records' },
	                  React.createElement(
	                    'span',
	                    { className: 'badge badge-pill badge-primary' },
	                    '0 - 0 - 0 - 0'
	                  )
	                )
	              )
	            )
	          )
	        ),
	        React.createElement(
	          'div',
	          { className: 'fighter-wrapper w-50 d-flex flex-column ml-4' },
	          React.createElement(
	            'div',
	            { className: 'fighter-2 card' },
	            React.createElement(
	              'div',
	              { className: 'selection text-center p-3' },
	              React.createElement(
	                'h3',
	                { className: 'my-4' },
	                'Fighter #2'
	              ),
	              React.createElement(FighterSearch, {
	                fighterSelected: this.fighter2Selected })
	            ),
	            React.createElement(
	              'div',
	              { className: 'detail' },
	              React.createElement('div', { className: 'img-wrapper', style: fighters[1] }),
	              React.createElement(
	                'div',
	                { className: 'card-block' },
	                React.createElement(
	                  'h4',
	                  { className: 'card-title name' },
	                  React.createElement(
	                    'div',
	                    { className: 'first-name' },
	                    'FirstName'
	                  ),
	                  React.createElement(
	                    'div',
	                    { className: 'last-name' },
	                    'LastName'
	                  )
	                ),
	                React.createElement(
	                  'div',
	                  { className: 'nickname' },
	                  '"Nickname nickname"'
	                ),
	                React.createElement(
	                  'h5',
	                  { className: 'records' },
	                  React.createElement(
	                    'span',
	                    { className: 'badge badge-pill badge-primary' },
	                    '0 - 0 - 0 - 0'
	                  )
	                )
	              )
	            )
	          )
	        )
	      )
	    );
	  }

	});

	ReactDOM.render(React.createElement(Mmath, null), document.getElementById('main-content'));

/***/ },
/* 1 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var React = __webpack_require__(2);

	var FighterSearch = __webpack_require__(3);

	var BetterThan = React.createClass({
	    displayName: 'BetterThan',

	    getInitialState: function getInitialState() {
	        return { fighter1: null, fighter2: null };
	    },
	    fighter1Selected: function fighter1Selected(fighter) {
	        this.setState({ fighter1: fighter });
	    },
	    fighter2Selected: function fighter2Selected(fighter) {
	        this.setState({ fighter2: fighter });
	    },
	    render: function render() {
	        return React.createElement(
	            'div',
	            { className: 'better-than' },
	            React.createElement(
	                'div',
	                { className: 'row' },
	                React.createElement(
	                    'div',
	                    { className: 'col-md-1 vs' },
	                    'Is'
	                ),
	                React.createElement(
	                    'div',
	                    { className: 'col-sm-4 fighter1' },
	                    React.createElement(FighterSearch, {
	                        fighterSelected: this.fighter1Selected })
	                ),
	                React.createElement(
	                    'div',
	                    { className: 'col-sm-2 vs' },
	                    'better than'
	                ),
	                React.createElement(
	                    'div',
	                    { className: 'col-sm-4 fighter2' },
	                    React.createElement(FighterSearch, {
	                        fighterSelected: this.fighter2Selected })
	                ),
	                React.createElement(
	                    'div',
	                    { className: 'col-md-1 vs' },
	                    '?'
	                )
	            ),
	            React.createElement(
	                'div',
	                { className: 'row' },
	                React.createElement(
	                    'div',
	                    { className: 'col-sm-12 fight-button' },
	                    this.state.fighter1 !== null && this.state.fighter2 !== null ? React.createElement(
	                        'button',
	                        { className: 'btn btn-danger',
	                            onClick: this.props.triggerSearch.bind(null, this.state.fighter1, this.state.fighter2) },
	                        'Find out !'
	                    ) : ''
	                )
	            )
	        );
	    }
	});

	module.exports = BetterThan;

/***/ },
/* 2 */
/***/ function(module, exports) {

	module.exports = React;

/***/ },
/* 3 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var React = __webpack_require__(2);

	var FighterTypeAhead = __webpack_require__(4);
	var FighterChip = __webpack_require__(5);
	var Loader = __webpack_require__(6);
	var FighterSearch = React.createClass({
	    displayName: 'FighterSearch',

	    //loads fighters based on the text search
	    loadFighters: function loadFighters(name) {

	        this.setState({ loading: true });

	        $.ajax({
	            method: 'POST',
	            url: '/api/fighters/query',
	            dataType: 'json',
	            data: JSON.stringify({ 'name': name }),
	            cache: false,
	            success: (function (data) {
	                console.log(data);
	                this.setState({ loading: false, fighters: data });
	            }).bind(this)
	        });
	    },
	    //initial state
	    getInitialState: function getInitialState() {
	        return {
	            fighters: [],
	            query: '',
	            selected: null,
	            loading: false
	        };
	    },
	    //Triggers the search
	    handleSearch: function handleSearch(e) {
	        console.log(e);
	        var query = e.target.value;
	        if (query.length >= 3) {
	            clearTimeout(this.state.timeout);
	            this.state.timeout = setTimeout((function () {
	                this.loadFighters(query);
	            }).bind(this), 300);
	        } else {
	            this.setState({ fighters: [] });
	        }
	    },

	    //Handles the click when the user clicks on the fighter
	    // he wants to choose
	    handleFighterSelection: function handleFighterSelection(fighter) {
	        this.setState({ selected: fighter });
	        this.props.fighterSelected(fighter, true);
	    },
	    //Remove the selected fighter
	    removeSelected: function removeSelected() {
	        this.setState({ selected: null, fighters: [] });
	        this.props.fighterSelected(null);
	    },
	    render: function render() {
	        if (this.state.selected === null) {
	            return React.createElement(
	                'div',
	                { className: 'fighter-search' },
	                this.state.loading ? React.createElement(
	                    'div',
	                    {
	                        className: 'search-loader' },
	                    React.createElement(Loader, null)
	                ) : '',
	                React.createElement('input', { className: 'form-control',
	                    type: 'text',
	                    onKeyUp: this.handleSearch,
	                    placeholder: 'Search for fighter'
	                }),
	                this.state.fighters.length > 0 ? React.createElement(FighterTypeAhead, {
	                    onClick: this.handleFighterSelection,
	                    fighters: this.state.fighters }) : ''
	            );
	        } else {
	            return React.createElement(FighterChip, { fighter: this.state.selected,
	                removable: true,
	                onClick: this.removeSelected });
	        }
	    }
	});

	module.exports = FighterSearch;

/***/ },
/* 4 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var React = __webpack_require__(2);

	var FighterTypeAhead = React.createClass({
	    displayName: 'FighterTypeAhead',

	    render: function render() {
	        return React.createElement(
	            'ul',
	            { className: 'fighters' },
	            this.props.fighters.map((function (fighter) {
	                var style = {
	                    backgroundImage: 'url(' + fighter.picture + ')'
	                };

	                return React.createElement(
	                    'li',
	                    {
	                        onClick: this.props.onClick.bind(null, fighter),
	                        key: fighter.id,
	                        'data-id': fighter.id },
	                    React.createElement('div', { className: 'icon', style: style }),
	                    fighter.name
	                );
	            }).bind(this))
	        );
	    }
	});
	module.exports = FighterTypeAhead;

/***/ },
/* 5 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var React = __webpack_require__(2);

	var FighterChip = React.createClass({
	    displayName: 'FighterChip',

	    render: function render() {
	        var picture = {
	            backgroundImage: 'url(' + this.props.fighter.picture + ')'
	        };

	        return React.createElement(
	            'div',
	            { className: 'fighter-chip' },
	            React.createElement('div', { className: 'picture', style: picture }),
	            React.createElement(
	                'span',
	                { className: 'name' },
	                this.props.fighter.name
	            ),
	            React.createElement(
	                'span',
	                { className: 'record' },
	                this.props.fighter.wins,
	                ' - ',
	                this.props.fighter.losses,
	                ' - ',
	                this.props.fighter.draws,
	                ' - ',
	                this.props.fighter.nc
	            ),
	            this.props.removable ? React.createElement(
	                'div',
	                { className: 'remove', onClick: this.props.onClick },
	                React.createElement('i', { className: 'fa fa-times',
	                    'aria-hidden': 'true' })
	            ) : ''
	        );
	    }

	});

	module.exports = FighterChip;

/***/ },
/* 6 */
/***/ function(module, exports, __webpack_require__) {

	"use strict";

	var React = __webpack_require__(2);

	var Loader = React.createClass({
	    displayName: "Loader",

	    render: function render() {
	        return React.createElement(
	            "div",
	            null,
	            React.createElement("div", { className: "loader" })
	        );
	    }

	});

	module.exports = Loader;

/***/ },
/* 7 */
/***/ function(module, exports, __webpack_require__) {

	'use strict';

	var React = __webpack_require__(2);

	var FighterChip = __webpack_require__(5);

	var FightResult = React.createClass({
	    displayName: 'FightResult',

	    render: function render() {
	        return React.createElement(
	            'div',
	            { className: 'result' },
	            this.props.results.map(function (fighter, i, arr) {
	                console.log(fighter);
	                return React.createElement(
	                    'div',
	                    { className: 'result-step' },
	                    React.createElement(FighterChip, {
	                        fighter: fighter,
	                        removable: false }),
	                    i < arr.length - 1 ? React.createElement(
	                        'div',
	                        {
	                            className: 'separator' },
	                        'Beat'
	                    ) : ''
	                );
	            })
	        );
	    }
	});

	module.exports = FightResult;

/***/ },
/* 8 */
/***/ function(module, exports, __webpack_require__) {

	var __WEBPACK_AMD_DEFINE_ARRAY__, __WEBPACK_AMD_DEFINE_RESULT__;/*!
	  Copyright (c) 2016 Jed Watson.
	  Licensed under the MIT License (MIT), see
	  http://jedwatson.github.io/classnames
	*/
	/* global define */

	(function () {
		'use strict';

		var hasOwn = {}.hasOwnProperty;

		function classNames () {
			var classes = [];

			for (var i = 0; i < arguments.length; i++) {
				var arg = arguments[i];
				if (!arg) continue;

				var argType = typeof arg;

				if (argType === 'string' || argType === 'number') {
					classes.push(arg);
				} else if (Array.isArray(arg)) {
					classes.push(classNames.apply(null, arg));
				} else if (argType === 'object') {
					for (var key in arg) {
						if (hasOwn.call(arg, key) && arg[key]) {
							classes.push(key);
						}
					}
				}
			}

			return classes.join(' ');
		}

		if (typeof module !== 'undefined' && module.exports) {
			module.exports = classNames;
		} else if (true) {
			// register as 'classnames', consistent with npm package name
			!(__WEBPACK_AMD_DEFINE_ARRAY__ = [], __WEBPACK_AMD_DEFINE_RESULT__ = function () {
				return classNames;
			}.apply(exports, __WEBPACK_AMD_DEFINE_ARRAY__), __WEBPACK_AMD_DEFINE_RESULT__ !== undefined && (module.exports = __WEBPACK_AMD_DEFINE_RESULT__));
		} else {
			window.classNames = classNames;
		}
	}());


/***/ }
/******/ ]);