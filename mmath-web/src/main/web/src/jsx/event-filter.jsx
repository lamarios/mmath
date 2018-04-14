import React from 'react';
import EventService from './services/EventService.jsx';


export default class EventFilters extends React.Component {
    constructor(props) {
        super(props);

        this.state = {organizations: [], selected: {}};

        this.service = new EventService();

        this.isSelected = this.isSelected.bind(this);
        this.onChange = this.onChange.bind(this);
        this.unselectAll = this.unselectAll.bind(this)
    }

    componentDidMount() {
        this.service.getOrganizationsForFilter()
            .then(res => {
                var selected = {};
                res.data.forEach((org, index) => {
                    selected[org.id] = true;
                });

                this.setState({organizations: res.data, selected});
            })
    }

    unselectAll(){
        var selected = this.state.selected;
        this.state.organizations.map((org, index) => {
            selected[org.id] =false;
        });

        this.setState({selected: selected}, () => this.props.onChange(this.state.selected));

    }

    onChange(e) {
        console.log(e.target.value, ' -> ', e.target.checked);
        var selected = this.state.selected;
        selected[e.target.value] = e.target.checked;
        this.setState({selected: selected}, () => this.props.onChange(this.state.selected));

    }

    isSelected(id) {
        return this.state.selected[id];
    }

    render() {
        return (<div className="EventFilter">
            <p>Filter events</p>

            <div className="checkboxes">
                {this.state.organizations.map((org, index) => {
                    return (
                        <label key={org.id}>
                            <input type="checkbox"
                                   value={org.id}
                                   name="organization"
                                   onChange={this.onChange}
                                   checked={this.state.selected[org.id]}
                            />
                            {org.name}
                        </label>
                    );
                })}
                <a onClick={this.unselectAll}>Show all organizations</a>
            </div>
        </div>);
    }
}