import React, {PureComponent} from 'react';
import {Menu, Divider, Image, Segment, Statistic} from 'semantic-ui-react';
import IconMenu from './IconMenu';
import {getTreePLESustainability} from './Requests';
import Logo from "../images/favicon.ico";

class NavigationBar extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      activeItem: '',
      showSidebar: false,
      sustainability: {
        stormwater: {factor: 0, worth: 0},
        co2Reduced: {factor: 0, worth: 0},
        biodiversity: {factor: 0, worth: 0},
        energyConserved: {factor: 0, worth: 0}
      }
    };
  }

  componentWillMount() {
    getTreePLESustainability()
      .then(response => {
        console.log(response);

        this.setState({sustainability: {
          stormwater: response.data.stormwater,
          co2Reduced: response.data.co2Reduced,
          biodiversity: response.data.biodiversity,
          energyConserved: response.data.energyConserved
        }});
      })
      .catch(error => {
        console.log(error);

      });
  }

  toggleSidebar = () => this.setState({showSidebar: !this.state.showSidebar});

  render() {
    const {sustainability} = this.state;
    console.log(sustainability);
    console.log(!!sustainability);

    const stormwater = !!sustainability.stormwater ? sustainability.stormwater : {factor: '--', worth: '--'};
    const co2Reduced = !!sustainability.co2Reduced ? sustainability.co2Reduced : {factor: '--', worth: '--'};
    const biodiversity = !!sustainability.biodiversity ? sustainability.biodiversity : {factor: '--'};
    const energyConserved = !!sustainability.energyConserved ? sustainability.energyConserved : {factor: '--', worth: '--'};

    return (
      <div>
        <Menu stackable fluid widths={5}>
          <Menu.Item>
            <Image as='Button' circular color='green' size='tiny' src={Logo} onClick={this.toggleSidebar}/>
          </Menu.Item>

          <Menu.Item
            name='stormwater'
            fitted='vertically'
          >
            <Segment.Group horizontal size='mini'>
              <Segment style={{display: 'flex', alignItems: 'center'}}>Stormwater Intercepted</Segment>
              <Segment>
                <Statistic horizontal size='mini' label='L/year' value={stormwater.factor.toFixed(2)}/>
                <Divider fitted/>
                <Statistic horizontal size='mini' label='$' value={stormwater.worth.toFixed(2)}/>
              </Segment>
            </Segment.Group>
          </Menu.Item>

          <Menu.Item
            name='co2Reduced'
            fitted='vertically'
          >
            <Segment.Group horizontal size='mini'>
              <Segment style={{display: 'flex', alignItems: 'center'}}>CO2 Reduced</Segment>
              <Segment>
                <Statistic horizontal size='mini' label='kg/year' value={co2Reduced.factor.toFixed(2)}/>
                <Divider fitted/>
                <Statistic horizontal size='mini' label='$' value={co2Reduced.worth.toFixed(2)}/>
              </Segment>
            </Segment.Group>
          </Menu.Item>

          <Menu.Item
            name='biodiversity'
            fitted='vertically'
          >
            <Segment.Group horizontal size='mini'>
              <Segment style={{display: 'flex', alignItems: 'center'}}>Biodiversity Index</Segment>
              <Segment>
                <Statistic horizontal size='mini' label='' value={biodiversity.factor.toFixed(5)}/>
              </Segment>
            </Segment.Group>
          </Menu.Item>

          <Menu.Item
            name='energyConserved'
            fitted='vertically'
          >
            <Segment.Group horizontal size='mini'>
              <Segment style={{display: 'flex', alignItems: 'center'}}>Energy Conserved</Segment>
              <Segment>
                <Statistic horizontal size='mini' label='kWh/year' value={energyConserved.factor.toFixed(2)}/>
                <Divider fitted/>
                <Statistic horizontal size='mini' label='$' value={energyConserved.worth.toFixed(2)}/>
              </Segment>
            </Segment.Group>
          </Menu.Item>
        </Menu>
        <IconMenu show={this.state.showSidebar}/>
      </div>
    );
  }
}

export default NavigationBar;