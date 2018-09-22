import React from 'react';
import styled from 'styled-components';
import Service from './Service';
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome'
import {faTrain, faAngry} from '@fortawesome/free-solid-svg-icons'
import Train from './Train';

const Div = styled.div`
  grid-area: content;
  text-align: center;
`;

const Content = styled.div`

`;

const Button = styled.button`
  border:none;
  font-size:20px;
  padding:10px;
  display: block;
  border-radius: 3px;
  margin:10px auto;
  cursor:pointer;
  color: ${props => props.theme.colors.text};
  font-weight: bold;
`;

const JumpOn = styled(Button)`
  background-color: #4caf50; 
`;

const JumpOff = styled(Button)`
  background-color: #f44336; 
`;

export default class Fighter extends React.Component {

    constructor(props) {
        super(props);

        this.service = new Service();
        this.state = {loggedIn: false};
        this.getFighter = this.getFighter.bind(this);
        this.jumpOn = this.jumpOn.bind(this);
        this.jumpOff = this.jumpOff.bind(this);
    }

    componentDidMount() {
        this.getFighter();
    }

    componentDidUpdate(prevProps) {
        if (this.props.match.params.fighter !== prevProps.match.params.fighter) {
            this.getFighter();
        }
    }


    getFighter() {
        this.service.getFighter(this.props.match.params.fighter)
            .then(res => this.setState({fighter: res}));
    }

    /**
     * On board !! tchoo tchoo
     */
    jumpOn() {
        this.service.jumpOn(this.state.fighter.id)
            .then(res => {
                if (res === true) {
                    this.getFighter();
                }
            });
    }

    /**
     * Nope...
     */
    jumpOff() {
        this.service.jumpOff(this.state.fighter.id)
            .then(res => {
                if (res === true) {
                    this.getFighter();
                }
            });
    }

    render() {
        return (<Div>
            {this.state.fighter && (<Content>
                    <h1>{this.state.fighter.name}</h1>
                    {this.state.fighter.count > 0 && <Train total={this.state.fighter.count}/>}
                    {this.state.fighter.count} {this.state.fighter.count === 1 ? "person is" : "people are"} on board
                    {this.state.fighter.loggedIn && !this.state.fighter.onBoard &&
                    <JumpOn onClick={() => this.jumpOn()}>
                        <FontAwesomeIcon icon={faTrain}/> Jump on board !
                    </JumpOn>}

                    {this.state.fighter.loggedIn && this.state.fighter.onBoard &&
                    <JumpOff onClick={() => this.jumpOff()}>
                        <FontAwesomeIcon icon={faAngry}/> Jump off board
                    </JumpOff>}

                    {!this.state.fighter.loggedIn && <p>
                        Log in to jump on board
                    </p>}
                </Content>
            )}
        </Div>);
    }
}