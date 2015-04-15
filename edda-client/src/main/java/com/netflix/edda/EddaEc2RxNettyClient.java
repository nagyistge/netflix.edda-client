/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.edda;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.core.type.TypeReference;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.ec2.AmazonEC2RxNettyClient;
import com.amazonaws.services.ec2.model.*;

import com.amazonaws.services.ServiceResult;
import com.amazonaws.services.PaginatedServiceResult;

import rx.Observable;

public class EddaEc2RxNettyClient extends EddaAwsRxNettyClient {
  public EddaEc2RxNettyClient(AwsConfiguration config, String vip, String region) {
    super(config, vip, region);
  }

  public AmazonEC2RxNettyClient readOnly() {
    return readOnly(AmazonEC2RxNettyClient.class);
  }

  public AmazonEC2RxNettyClient wrapAwsClient(AmazonEC2RxNettyClient delegate) {
    return wrapAwsClient(AmazonEC2RxNettyClient.class, delegate);
  }

  public Observable<PaginatedServiceResult<DescribeClassicLinkInstancesResult>> describeClassicLinkInstances() {
    return describeClassicLinkInstances(new DescribeClassicLinkInstancesRequest());
  }

  public Observable<PaginatedServiceResult<DescribeClassicLinkInstancesResult>> describeClassicLinkInstances(
    final DescribeClassicLinkInstancesRequest request
  ) {
    return Observable.defer(() -> {
      validateEmpty("Filter", request.getFilters());

      TypeReference<List<ClassicLinkInstance>> ref = new TypeReference<List<ClassicLinkInstance>>(){
      };

      String url = config.url() + "/api/v2/aws/classicLinkInstances;_expand";
      return doGet(url).map(sr -> {
        try {
          List<ClassicLinkInstance> instances = parse(ref, sr.result);

          List<String> ids = request.getInstanceIds();
          if (shouldFilter(ids)) {
            List<ClassicLinkInstance> is = new ArrayList<ClassicLinkInstance>();
            for (ClassicLinkInstance i : instances) {
              if (matches(ids, i.getInstanceId()))
                is.add(i);
            }
            instances = is;
          }

          return new PaginatedServiceResult<DescribeClassicLinkInstancesResult>(
            sr.startTime,
            null,
            new DescribeClassicLinkInstancesResult().withInstances(instances)
          );
        }
        catch (IOException e) {
          throw new AmazonClientException("Faled to parse " + url, e);
        }
      });
    });
  }

  public Observable<ServiceResult<DescribeImagesResult>> describeImages() {
    return describeImages(new DescribeImagesRequest());
  }

  public Observable<ServiceResult<DescribeImagesResult>> describeImages(
    final DescribeImagesRequest request
  ) {
    return Observable.defer(() -> {
      validateEmpty("ExecutableUsers", request.getExecutableUsers());
      validateEmpty("Filter", request.getFilters());

      TypeReference<List<Image>> ref = new TypeReference<List<Image>>() {};
      String url = config.url() + "/api/v2/aws/images;_expand";
      return doGet(url).map(sr -> {
        try {
          List<Image> images = parse(ref, sr.result);

          List<String> owners = request.getOwners();
          List<String> ids = request.getImageIds();
          if (shouldFilter(owners) || shouldFilter(ids)) {
            List<Image> is = new ArrayList<Image>();
            for (Image i : images) {
              if (matches(owners, i.getOwnerId()) && matches(ids, i.getImageId()))
                is.add(i);
            }
            images = is;
          }

          return new ServiceResult<DescribeImagesResult>(
            sr.startTime,
            new DescribeImagesResult().withImages(images)
          );
        }
        catch (IOException e) {
          throw new AmazonClientException("Faled to parse " + url, e);
        }
      });
    });
  }

  public Observable<PaginatedServiceResult<DescribeInstancesResult>> describeInstances() {
    return describeInstances(new DescribeInstancesRequest());
  }

  public Observable<PaginatedServiceResult<DescribeInstancesResult>> describeInstances(
    final DescribeInstancesRequest request
  ) {
    return Observable.defer(() -> {
      validateEmpty("Filter", request.getFilters());

      TypeReference<List<Reservation>> ref = new TypeReference<List<Reservation>>() {};
      String url = config.url() + "/api/v2/aws/instances;_expand";
      return doGet(url).map(sr -> {
        try {
          List<Reservation> reservations = parse(ref, sr.result);

          List<String> ids = request.getInstanceIds();
          if (shouldFilter(ids)) {
            List<Reservation> rs = new ArrayList<Reservation>();
            for (Reservation r : reservations) {
              List<Instance> is = new ArrayList<Instance>();
              for (Instance i : r.getInstances()) {
                if (matches(ids, i.getInstanceId()))
                  is.add(i);
              }
              if (is.size() > 0)
                rs.add(r.withInstances(is));
            }
            reservations = rs;
          }
          return new PaginatedServiceResult<DescribeInstancesResult>(
            sr.startTime,
            null,
            new DescribeInstancesResult().withReservations(reservations)
          );
        }
        catch (IOException e) {
          throw new AmazonClientException("Faled to parse " + url, e);
        }
      });
    });
  }

  public Observable<PaginatedServiceResult<DescribeReservedInstancesOfferingsResult>> describeReservedInstancesOfferings() {
    return describeReservedInstancesOfferings(new DescribeReservedInstancesOfferingsRequest());
  }

  public Observable<PaginatedServiceResult<DescribeReservedInstancesOfferingsResult>> describeReservedInstancesOfferings(
    final DescribeReservedInstancesOfferingsRequest request
  ) {
    return Observable.defer(() -> {
      validateEmpty("Filter", request.getFilters());
      validateEmpty("AvailabilityZone", request.getAvailabilityZone());
      validateEmpty("IncludeMarketplace", request.getIncludeMarketplace());
      validateEmpty("InstanceTenancy", request.getInstanceTenancy());
      validateEmpty("InstanceType", request.getInstanceType());
      validateEmpty("OfferingType", request.getOfferingType());
      validateEmpty("ProductDescription", request.getProductDescription());

      TypeReference<List<ReservedInstancesOffering>> ref = new TypeReference<List<ReservedInstancesOffering>>() {};
      String url = config.url() + "/api/v2/aws/reservedInstancesOfferings;_expand";
      return doGet(url).map(sr -> {
        try {
          List<ReservedInstancesOffering> reservedInstancesOfferings = parse(ref, sr.result);

          List<String> ids = request.getReservedInstancesOfferingIds();
          if (shouldFilter(ids)) {
            List<ReservedInstancesOffering> rs = new ArrayList<ReservedInstancesOffering>();
            for (ReservedInstancesOffering r : reservedInstancesOfferings) {
              if (matches(ids, r.getReservedInstancesOfferingId()))
                rs.add(r);
            }
            reservedInstancesOfferings = rs;
          }
          return new PaginatedServiceResult<DescribeReservedInstancesOfferingsResult>(
            sr.startTime,
            null,
            new DescribeReservedInstancesOfferingsResult().withReservedInstancesOfferings(reservedInstancesOfferings)
          );
        }
        catch (IOException e) {
          throw new AmazonClientException("Faled to parse " + url, e);
        }
      });
    });
  }

  public Observable<ServiceResult<DescribeSecurityGroupsResult>> describeSecurityGroups() {
    return describeSecurityGroups(new DescribeSecurityGroupsRequest());
  }

  public Observable<ServiceResult<DescribeSecurityGroupsResult>> describeSecurityGroups(
    final DescribeSecurityGroupsRequest request
  ) {
    return Observable.defer(() -> {
      validateEmpty("Filter", request.getFilters());

      TypeReference<List<SecurityGroup>> ref = new TypeReference<List<SecurityGroup>>() {};
      String url = config.url() + "/api/v2/aws/securityGroups;_expand";
      return doGet(url).map(sr -> {
        try {
          List<SecurityGroup> securityGroups = parse(ref, sr.result);

          List<String> names = request.getGroupNames();
          List<String> ids = request.getGroupIds();
          if (shouldFilter(names) || shouldFilter(ids)) {
            List<SecurityGroup> sgs = new ArrayList<SecurityGroup>();
            for (SecurityGroup sg : securityGroups) {
              if (matches(names, sg.getGroupName()) && matches(ids, sg.getGroupId()))
                sgs.add(sg);
            }
            securityGroups = sgs;
          }

          return new ServiceResult<DescribeSecurityGroupsResult>(
            sr.startTime,
            new DescribeSecurityGroupsResult().withSecurityGroups(securityGroups)
          );
        }
        catch (IOException e) {
          throw new AmazonClientException("Faled to parse " + url, e);
        }
      });
    });
  }

  public Observable<ServiceResult<DescribeSubnetsResult>> describeSubnets() {
    return describeSubnets(new DescribeSubnetsRequest());
  }

  public Observable<ServiceResult<DescribeSubnetsResult>> describeSubnets(
    final DescribeSubnetsRequest request
  ) {
    return Observable.defer(() -> {
    validateEmpty("Filter", request.getFilters());

    TypeReference<List<Subnet>> ref = new TypeReference<List<Subnet>>() {};
    String url = config.url() + "/api/v2/aws/subnets;_expand";
      return doGet(url).map(sr -> {
        try {
          List<Subnet> subnets = parse(ref, sr.result);

          List<String> ids = request.getSubnetIds();
          if (shouldFilter(ids)) {
            List<Subnet> ss = new ArrayList<Subnet>();
            for (Subnet s : subnets) {
              if (matches(ids, s.getSubnetId()))
                ss.add(s);
            }
            subnets = ss;
          }

          return new ServiceResult<DescribeSubnetsResult>(
            sr.startTime,
            new DescribeSubnetsResult().withSubnets(subnets)
          );
        }
        catch (IOException e) {
          throw new AmazonClientException("Faled to parse " + url, e);
        }
      });
    });
  }

  public Observable<PaginatedServiceResult<DescribeVolumesResult>> describeVolumes() {
    return describeVolumes(new DescribeVolumesRequest());
  }

  public Observable<PaginatedServiceResult<DescribeVolumesResult>> describeVolumes(
    final DescribeVolumesRequest request
  ) {
    return Observable.defer(() -> {
    validateEmpty("Filter", request.getFilters());

    TypeReference<List<Volume>> ref = new TypeReference<List<Volume>>() {};
    String url = config.url() + "/api/v2/aws/volumes;_expand";
      return doGet(url).map(sr -> {
        try {
          List<Volume> volumes = parse(ref, sr.result);

          List<String> ids = request.getVolumeIds();
          if (shouldFilter(ids)) {
            List<Volume> vs = new ArrayList<Volume>();
            for (Volume v : volumes) {
              if (matches(ids, v.getVolumeId()))
                vs.add(v);
            }
            volumes = vs;
          }

          return new PaginatedServiceResult<DescribeVolumesResult>(
            sr.startTime,
            null,
            new DescribeVolumesResult().withVolumes(volumes)
          );
        }
        catch (IOException e) {
          throw new AmazonClientException("Faled to parse " + url, e);
        }
      });
    });
  }

  public Observable<ServiceResult<DescribeVpcsResult>> describeVpcs() {
    return describeVpcs(new DescribeVpcsRequest());
  }

  public Observable<ServiceResult<DescribeVpcsResult>> describeVpcs(
    final DescribeVpcsRequest request
  ) {
    return Observable.defer(() -> {
    validateEmpty("Filter", request.getFilters());

    TypeReference<List<Vpc>> ref = new TypeReference<List<Vpc>>() {};
    String url = config.url() + "/api/v2/aws/vpcs;_expand";
      return doGet(url).map(sr -> {
        try {
          List<Vpc> vpcs = parse(ref, sr.result);

          List<String> ids = request.getVpcIds();
          if (shouldFilter(ids)) {
            List<Vpc> vs = new ArrayList<Vpc>();
            for (Vpc v : vpcs) {
              if (matches(ids, v.getVpcId()))
                vs.add(v);
            }
            vpcs = vs;
          }

          return new ServiceResult<DescribeVpcsResult>(
            sr.startTime,
            new DescribeVpcsResult().withVpcs(vpcs)
          );
        }
        catch (IOException e) {
          throw new AmazonClientException("Faled to parse " + url, e);
        }
      });
    });
  }
}