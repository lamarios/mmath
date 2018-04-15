import React from 'react';
import EventService from './services/EventService.jsx';


export default class EventFilters extends React.Component {
    constructor(props) {
        super(props);

        this.state = {organizations: [], selected: {}, everythingElseDisabled: false};

        this.service = new EventService();

        this.isSelected = this.isSelected.bind(this);
        this.onChange = this.onChange.bind(this);
    }

    componentDidMount() {
        this.service.getOrganizationsForFilter()
            .then(res => {
                var selected = {};
                res.data.forEach((org, index) => {
                    selected[org.id] = true;
                });

                selected['all'] = false;

                this.setState({organizations: res.data, selected}, () => this.props.onChange(this.state.selected));
            })
    }


    onChange(e) {

        var selected = this.state.selected;
        selected[e.target.value] = e.target.checked;

        var everythingElseDisabled = false;

        // setting the state of everything else
        this.state.organizations.forEach((org, index) => {
            if (!selected[org.id]) {
                everythingElseDisabled = true;
            }
        });

        if(everythingElseDisabled){
            selected['all'] = false;
        }

        this.setState({
            selected: selected,
            everythingElseDisabled: everythingElseDisabled
        }, () => this.props.onChange(this.state.selected));


    }


    isSelected(id) {
        return this.state.selected[id];
    }

    render() {
        return (<div className="EventFilter">
            <p>Select organizations</p>

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
                <label className={this.state.everythingElseDisabled?"disabled":""}>
                    <input type="checkbox"
                           value="all"
                           name="organizations"
                           onChange={this.onChange}
                           checked={this.state.selected['all']}
                           disabled={this.state.everythingElseDisabled}
                    />
                    Everything else
                </label>

            </div>
        </div>);
    }
}