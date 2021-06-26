package com.caluli.proposalapp.model;

import java.io.Serializable;

public class Proposal implements Serializable {

    public  String proposal_id;
    public  String proposal_group;
    public  String proposal_title;
    public  String proposal_objective;
    public  String proposal_problem_statement;
    public  String proposal_organization;
    public  String proposal_organization_address;
    public  String proposal_supervisor;
    public  String proposal_comments;
    public  String proposal_status;

    public Proposal() {
    }

    public Proposal( String proposal_id, String proposal_group, String proposal_title, String proposal_objective, String proposal_problem_statement, String proposal_organization,  String proposal_organization_address, String proposal_supervisor,  String proposal_comments, String proposal_status) {

        this.proposal_id = proposal_id;
        this.proposal_group = proposal_group;
        this.proposal_title = proposal_title;
        this.proposal_objective = proposal_objective;
        this.proposal_problem_statement = proposal_problem_statement;
        this.proposal_organization = proposal_organization;
        this.proposal_organization_address = proposal_organization_address;
        this.proposal_supervisor = proposal_supervisor;
        this.proposal_comments = proposal_comments;
        this.proposal_status = proposal_status;
    }

    public String getProposal_id() {
        return proposal_id;
    }

    public void setProposal_id(String proposal_id) {
        this.proposal_id = proposal_id;
    }

    public String getProposal_group() {
        return proposal_group;
    }

    public void setProposal_group(String proposal_group) {
        this.proposal_group = proposal_group;
    }

    public String getProposal_title() {
        return proposal_title;
    }

    public void setProposal_title(String proposal_title) {
        this.proposal_title = proposal_title;
    }

    public String getProposal_objective() {
        return proposal_objective;
    }

    public void setProposal_objective(String proposal_objective) {
        this.proposal_objective = proposal_objective;
    }

    public String getProposal_problem_statement() {
        return proposal_problem_statement;
    }

    public void setProposal_problem_statement(String proposal_problem_statement) {
        this.proposal_problem_statement = proposal_problem_statement;
    }

    public String getProposal_organization() {
        return proposal_organization;
    }

    public void setProposal_organization(String proposal_organization) {
        this.proposal_organization = proposal_organization;
    }


    public String getProposal_organization_address() {
        return proposal_organization_address;
    }

    public void setProposal_organization_address(String proposal_organization_address) {
        this.proposal_organization_address = proposal_organization_address;
    }

    public String getProposal_supervisor() {
        return proposal_supervisor;
    }

    public void setProposal_supervisor(String proposal_supervisor) {
        this.proposal_supervisor = proposal_supervisor;
    }

    public String getProposal_comments() {
        return proposal_comments;
    }

    public void setProposal_comments(String proposal_comments) {
        this.proposal_comments = proposal_comments;
    }

    public String getProposal_status() {
        return proposal_status;
    }

    public void setProposal_status(String proposal_status) {
        this.proposal_status = proposal_status;
    }



}

