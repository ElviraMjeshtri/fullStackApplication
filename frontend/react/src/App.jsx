import {Wrap, WrapItem, Spinner, Text} from '@chakra-ui/react';
import SidebarWithHeader from "./components/shared/sidebar.jsx";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js";
import CardWithImage from "./components/customer/card.jsx";
import CreateCustomerDrawer from "./components/customer/CreateCustomerDrawer.jsx";
import {errorNotification} from "./services/notification.js";


const  App = () => {
    const [customers, setCustomers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const fetchCustomers = ()=> {
        setLoading(true);
        getCustomers().then(response => {
            setCustomers(response.data)
        }).catch(err =>{
            setError(err.response.data.message)
            errorNotification(
                err.code,
                err.response.data.message
            )
        }).finally(() => {
                setLoading(false);
            }
        )
    }
    useEffect(()=>{
      fetchCustomers()
    }, [])

    if(loading){
        return (
            <SidebarWithHeader>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            </SidebarWithHeader>
            )
    }
    if(error){
        return (
            <SidebarWithHeader>
                <CreateCustomerDrawer
                    fetchCustomers = {fetchCustomers}
                />
                <Text mt={5}>Ooops there was an error</Text>
            </SidebarWithHeader>
        )
    }

    if(customers.length <=0){
        return (
            <SidebarWithHeader>
                <CreateCustomerDrawer
                    fetchCustomers = {fetchCustomers}
                />
               <Text mt={5}>No costumers Available</Text>
            </SidebarWithHeader>
        )
    }
    return (
        <SidebarWithHeader>
            <CreateCustomerDrawer
                fetchCustomers = {fetchCustomers}
            />
            <Wrap justify={"center"} spacing={"30px"}>
                {customers.map((customer, index)=>(
                    <WrapItem key={index}>
                        <CardWithImage
                            {...customer}
                            imageNumber={index}
                            fetchCustomers = {fetchCustomers}
                        />
                    </WrapItem>

                ))}
            </Wrap>

        </SidebarWithHeader>
    )

}
export default App;