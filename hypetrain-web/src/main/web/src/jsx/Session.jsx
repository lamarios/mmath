import React from 'react';
import styled from 'styled-components';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome'
import {faReddit} from '@fortawesome/free-brands-svg-icons'
import Service from './Service';
import { NavLink } from 'react-router-dom';


const Content = styled.div`
  grid-area: session;
  text-align: right;
`;

const Login = styled.button`
  border:none;
  background-color: #fff;
  margin:5px;
  padding:10px;
  border-radius: 2px;
  color: ${props => props.theme.colors.reddit};
  font-weight: bold;
  font-size:18px;
  cursor: pointer;
  
  svg{
    margin-right: 5px;
  }
`;


const LoggedInLink = styled(NavLink)`
  text-decoration: none;
  border: none;
  outline:0;
  p {
  margin:0 10px;
  font-size: 13px;
  color: ${props => props.theme.colors.text};
  }
`;

export default class Session extends React.Component {

    constructor(props) {
        super(props);

        this.state = {loading: true, loggedIn: false, username: ''};
        this.service = new Service();
        this.getUser = this.getUser.bind(this);
    }


    componentDidMount() {
        this.getUser();
    }


    getUser() {
        this.service.me()
            .then(u => {
                console.log(u);
                this.setState({loggedIn: true, username: u, loading: false})
            })
            .catch(e => this.setState({loggedIn: false, username: '', loading: false}))
    }

    render() {
        return (<Content>
            {!this.state.loading && !this.state.loggedIn &&
            (<a href="/login">
                <Login>
                    <FontAwesomeIcon icon={faReddit}/>
                    Log in with Reddit
                </Login>
            </a>)}

            {!this.state.loading && this.state.loggedIn === true &&
            (<LoggedInLink to="/me">
                <Login>
                    <FontAwesomeIcon icon={faReddit}/>
                    {this.state.username}
                </Login>
                <p>Manage your hype</p>
            </LoggedInLink>)}
        </Content>);
    }

}