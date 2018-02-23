var React = require('react');
var createReactClass = require('create-react-class');

var Loader = createReactClass({
    render: function () {
        return (
            <div>
                <div className="loader"></div>
            </div>
        );
    }

});

module.exports = Loader;