import React, { useEffect, useState } from "react";
import Cardlist from "./cardlist";

export default function Cards(props){
    
    return(
        <>
        <div className="row justify-content-center">
            <div className="col-md-8 offset-md-2 mx-5 py-4 text-center">
                <h1 className="text-center text-break" style={{color:'navy'}} >Poslednja mesta!</h1>
            </div>
        </div>
        <div style={{borderTop:'solid',borderBottom:'solid', borderColor:'darkorange'}}>
            <div className="row row-cols-1 row-cols-md-3 g-4 my-2 justify-content-center" >
                {props.content && props.content.map((item,index)=>(
                    <Cardlist key={index} name={item.name} destination={item.destination} city={item.city} country={item.country} startDate={item.startDate} endDate={item.endDate} price={item.price} transport={item.transportation} id={item.id} picture={item.bigPicture}/>
                ))}
            </div>
        </div>
        </>
    );
}