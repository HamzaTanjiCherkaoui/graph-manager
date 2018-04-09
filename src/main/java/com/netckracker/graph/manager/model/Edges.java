/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.netckracker.graph.manager.model;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;


/**
 *
 * @author eliza
 */
@Entity
@Table
public class Edges implements Serializable  {
    @Id   
    @Column(name = "edge_id") 
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String edgeId;
    
    @ManyToOne
    @JoinColumn(name = "start_node_id")
    private Node startNode;
    
    
    @ManyToOne
    @JoinColumn(name = "end_node_id")
    private Node endNode;
    
}
