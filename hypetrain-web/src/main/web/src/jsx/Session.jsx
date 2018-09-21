import React from 'react';
import styled from 'styled-components';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faReddit } from '@fortawesome/free-brands-svg-icons'

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
  color:#FF4500;
  font-weight: bold;
  font-size:18px;
  cursor: pointer;
  
  svg{
    margin-right: 5px;
  }
`;

export default class Session extends React.Component {


    render() {
        return (<Content>
            <Login>
                <FontAwesomeIcon icon={faReddit}/>
                Log in with reddit
            </Login>
        </Content>);
    }

}