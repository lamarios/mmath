import React from 'react';
import styled from 'styled-components';
import Service from './Service';
import {NavLink} from 'react-router-dom';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome'
import {faBan} from '@fortawesome/free-solid-svg-icons'


const Div = styled.div`
  grid-area: content;
  text-align: center;
  margin-top:20px;
`;

const Hype = styled.div`
  display:grid;
  text-align: left;
  grid-template-areas: 'name actions';
  grid-template-columns: 1fr 30px;
  margin: 5px auto;
  border-bottom: 1px dashed ${props => props.theme.colors.text};
  padding: 2px 0;
`;

const HypeLink = styled(NavLink)`
  grid-area: name;
  text-decoration: none;
  color: ${props => props.theme.colors.text};
`;

const HypeActions = styled.div`
  grid-area: actions;
  
`;


const DeleteButton = styled.button`
  border:none;
  background-color: ${props => props.theme.colors.reddit};
  color: ${props => props.theme.colors.text};
  border-radius: 3px;
  font-size:20px;
  padding:5px; 
  cursor:pointer;
`;

export default class Me extends React.Component {
    constructor(props) {
        super(props);

        this.state = {myHype: []};
        this.myHype = this.myHype.bind(this);
        this.jumpOff = this.jumpOff.bind(this);
        this.service = new Service();
    }

    componentDidMount() {
        this.myHype();
    }

    myHype() {
        this.service.getMyHype()
            .then(res => this.setState({myHype: res}));
    }

    jumpOff(fighter){
        this.service.jumpOff(fighter)
            .then(res => this.myHype());
    }

    render() {
        console.table(this.state.myHype);
        return (<Div>
                <h2>Manage your hype !</h2>
                {this.state.myHype.length == 0 && <p>No so much hype in there.</p>}
                {this.state.myHype.map(h => {
                    return (<Hype key={h.id}>
                        <HypeLink to={"/fighter/" + h.id}>{h.fighterName}</HypeLink>
                        <HypeActions>
                            <DeleteButton onClick={(e) => this.jumpOff(h.id)}>
                                <FontAwesomeIcon icon={faBan}/>
                            </DeleteButton>
                        </HypeActions>
                    </Hype>)
                })}
            </Div>

        );
    }
}